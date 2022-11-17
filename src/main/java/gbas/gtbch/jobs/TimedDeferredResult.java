package gbas.gtbch.jobs;

import org.springframework.web.context.request.async.DeferredResult;

class TimedDeferredResult<T> {
    long time;

    long getTime() {
        return time;
    }

    DeferredResult<T> getDeferredResult() {
        return deferredResult;
    }

    DeferredResult<T> deferredResult;

    TimedDeferredResult(long time, DeferredResult<T> deferredResult) {
        this.time = time;
        this.deferredResult = deferredResult;
    }
}
