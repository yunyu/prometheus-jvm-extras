package in.yunyul.prometheus.extras;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class AdditionalVertxExports extends Collector {
    private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static final String EVENT_LOOP_THREAD_PREFIX = "vert.x-eventloop-thread-";

    private int getNumberOfEventLoopThreads() {
        int eventLoopThreads = 0;
        for (ThreadInfo threadInfo : threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds())) {
            if (threadInfo.getThreadName().startsWith(EVENT_LOOP_THREAD_PREFIX)) {
                ++eventLoopThreads;
            }
        }
        return eventLoopThreads;
    }


    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>(1);
        mfs.add(new GaugeMetricFamily("vertx_event_loop_threads", "The number of Vert.x event loop threads used",
                getNumberOfEventLoopThreads()));
        return mfs;
    }
}