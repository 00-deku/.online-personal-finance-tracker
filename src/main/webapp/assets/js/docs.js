/**
 * ONLINE PERSONAL FINANCE MANAGEMENT SYSTEM
 * File: docs.js
 * Description: Docs page table-of-contents highlighting and the live thread-pool monitor.
 */

document.addEventListener('DOMContentLoaded', function () {
    initTocHighlight();
    initLiveMonitor();
});

/**
 * Highlights the table-of-contents link for the section currently in view.
 */
function initTocHighlight() {
    const links = document.querySelectorAll('#docsToc a');
    if (!links.length || !('IntersectionObserver' in window)) return;

    const byId = {};
    links.forEach(link => { byId[link.getAttribute('href').slice(1)] = link; });

    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                links.forEach(l => l.classList.remove('active'));
                const link = byId[entry.target.id];
                if (link) link.classList.add('active');
            }
        });
    }, { rootMargin: '-80px 0px -70% 0px' });

    document.querySelectorAll('.docs-section').forEach(section => observer.observe(section));
}

/**
 * Polls /api/concurrency every 3 seconds and renders pool stats, activity sparkline,
 * recent batches and recent tasks.
 */
function initLiveMonitor() {
    const dot = document.getElementById('liveDot');
    if (!dot) return;

    const endpoint = (window.FINANCE_CTX || '') + '/api/concurrency';
    const status = document.getElementById('liveStatus');

    async function refresh() {
        try {
            const res = await fetch(endpoint, { cache: 'no-store' });
            if (!res.ok) throw new Error('HTTP ' + res.status);
            const data = await res.json();
            render(data);
            dot.classList.add('on');
            status.textContent = 'Live · updated ' + new Date().toLocaleTimeString();
        } catch (e) {
            dot.classList.remove('on');
            status.textContent = 'Monitor unavailable (' + e.message + ')';
        }
    }

    refresh();
    setInterval(() => {
        if (!document.hidden) refresh();
    }, 3000);
}

function render(data) {
    setText('mPool', data.poolSize);
    setText('mActive', data.activeThreads);
    setText('mCompleted', data.completed);
    setText('mFailed', data.failed);
    setText('mBatches', data.batches);
    setText('mSaved', formatMs(data.totalSavedMs));

    renderSparkline(data.samples || []);
    renderThreadChips(data);
    renderBatches(data.batchesRecent || []);
    renderTasks(data.tasksRecent || []);
}

function renderSparkline(samples) {
    const svg = document.getElementById('sparkline');
    if (!svg) return;

    // Samples arrive newest first; draw oldest on the left.
    const points = samples.slice().reverse();
    if (points.length < 2) {
        svg.innerHTML = '<text x="150" y="36" text-anchor="middle" font-size="9" style="fill: var(--text-muted);">Collecting samples…</text>';
        return;
    }

    const max = Math.max(4, ...points.map(p => Math.max(p.poolSize, p.active)));
    const w = 300, h = 64, pad = 4;
    const x = i => (i / (points.length - 1)) * w;
    const y = v => h - pad - (v / max) * (h - pad * 2);

    const line = key => points.map((p, i) => (i ? 'L' : 'M') + x(i).toFixed(1) + ' ' + y(p[key]).toFixed(1)).join(' ');
    const area = line('active') + ' L' + w + ' ' + h + ' L0 ' + h + ' Z';

    svg.innerHTML =
        '<path d="' + area + '" style="fill: var(--success); opacity: 0.12;"/>' +
        '<path d="' + line('poolSize') + '" style="fill: none; stroke: var(--text-muted);" stroke-width="1.5" stroke-dasharray="3 3" vector-effect="non-scaling-stroke"/>' +
        '<path d="' + line('active') + '" style="fill: none; stroke: var(--success);" stroke-width="2" vector-effect="non-scaling-stroke"/>';
}

/**
 * One chip per worker thread seen in recent tasks; highlights as many as are active right now.
 */
function renderThreadChips(data) {
    const box = document.getElementById('threadChips');
    if (!box) return;

    const names = new Set();
    (data.tasksRecent || []).forEach(t => names.add(t.thread));
    const sorted = Array.from(names).sort((a, b) => a.localeCompare(b, undefined, { numeric: true }));

    if (!sorted.length) {
        box.innerHTML = '<span class="text-muted">Worker threads will appear here after the first parallel page load.</span>';
        return;
    }

    let busy = data.activeThreads || 0;
    box.innerHTML = sorted.map(name => {
        const cls = busy-- > 0 ? 'thread-chip busy' : 'thread-chip';
        return '<span class="' + cls + '">' + escapeHtml(name) + '</span>';
    }).join('') +
        '<span class="text-muted" style="font-size: 0.72rem; align-self: center;">— solid line: active threads, dashed: pool size</span>';
}

function renderBatches(batches) {
    const body = document.getElementById('batchRows');
    if (!body) return;
    if (!batches.length) {
        body.innerHTML = '<tr><td colspan="5" class="text-muted">No batches yet. Open a dashboard or report to generate some.</td></tr>';
        return;
    }
    body.innerHTML = batches.map(b =>
        '<tr>' +
        '<td>' + escapeHtml(b.name) + ' <span class="text-muted">· ' + timeAgo(b.finishedAt) + '</span></td>' +
        '<td class="text-right font-mono">' + b.tasks + '</td>' +
        '<td class="text-right font-mono">' + b.threads + '</td>' +
        '<td class="text-right font-mono">' + b.wallMs + ' ms</td>' +
        '<td class="text-right font-mono">' + b.sequentialMs + ' ms</td>' +
        '</tr>'
    ).join('');
}

function renderTasks(tasks) {
    const body = document.getElementById('taskRows');
    if (!body) return;
    if (!tasks.length) {
        body.innerHTML = '<tr><td colspan="4" class="text-muted">No tasks yet.</td></tr>';
        return;
    }
    body.innerHTML = tasks.slice(0, 12).map(t =>
        '<tr>' +
        '<td>' + escapeHtml(t.label) + ' <span class="text-muted">· ' + escapeHtml(t.batch) + '</span></td>' +
        '<td class="font-mono text-muted">' + escapeHtml(t.thread) + '</td>' +
        '<td class="text-right font-mono">' + t.durationMs + ' ms</td>' +
        '<td class="text-right"><span class="badge">' + escapeHtml(t.status) + '</span></td>' +
        '</tr>'
    ).join('');
}

function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = value;
}

function formatMs(ms) {
    return ms >= 1000 ? (ms / 1000).toFixed(1) + ' s' : ms + ' ms';
}

function timeAgo(ts) {
    const s = Math.max(0, Math.round((Date.now() - ts) / 1000));
    if (s < 60) return s + 's ago';
    if (s < 3600) return Math.round(s / 60) + 'm ago';
    return Math.round(s / 3600) + 'h ago';
}

function escapeHtml(str) {
    return String(str == null ? '' : str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}
