<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- Shows how the current page's data was loaded across worker threads (set by the servlet as "parallelBatch") --%>
<c:set var="pb" value="${requestScope.parallelBatch}"/>
<c:if test="${not empty pb and pb.taskCount > 0}">
    <details class="card parallel-panel">
        <summary>
            <div>
                <div class="card-title">Parallel execution</div>
                <div class="parallel-summary-stats mt-2">
                    <span><strong>${pb.taskCount}</strong> tasks</span>
                    <span><strong>${pb.threadCount}</strong> threads</span>
                    <span><strong>${pb.wallTimeMs} ms</strong> wall time</span>
                    <span><strong>${pb.sequentialTimeMs} ms</strong> if run one by one</span>
                    <c:if test="${pb.wallTimeMs > 0 and pb.sequentialTimeMs > pb.wallTimeMs}">
                        <span><strong><fmt:formatNumber value="${pb.sequentialTimeMs / pb.wallTimeMs}" maxFractionDigits="1"/>&times;</strong> faster</span>
                    </c:if>
                </div>
            </div>
        </summary>

        <div class="timeline">
            <c:forEach items="${pb.records}" var="r">
                <div class="timeline-row">
                    <div class="timeline-label" title="${r.label}">
                        ${r.label}
                        <span class="timeline-thread">${r.threadName}</span>
                    </div>
                    <div class="timeline-track">
                        <div class="timeline-bar ${r.success ? '' : 'failed'}"
                             style="left: ${r.startOffsetMs * 100.0 / pb.timelineSpanMs}%; width: ${r.durationMs * 100.0 / pb.timelineSpanMs}%;"></div>
                    </div>
                    <div class="timeline-ms">${r.durationMs} ms</div>
                </div>
            </c:forEach>
        </div>
        <p class="timeline-note">
            Bars that overlap ran at the same time on different threads.
            <a href="${pageContext.request.contextPath}/docs#multithreading" style="text-decoration: underline;">How this works</a>
        </p>
    </details>
</c:if>
