package in.yunyul.prometheus.extras;

import io.prometheus.client.Collector;
import io.prometheus.client.GaugeMetricFamily;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class AdditionalJVMExports extends Collector {
    private static final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    private static final FileSystem defaultFs = FileSystems.getDefault();

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> mfs = new ArrayList<>();

        mfs.add(new GaugeMetricFamily("os_load_average", "The system load average for the last minute",
                osBean.getSystemLoadAverage()));
        mfs.add(new GaugeMetricFamily("os_avail_processors", "The number of processors available to the Java virtual machine",
                osBean.getAvailableProcessors()));

        GaugeMetricFamily osInfo = new GaugeMetricFamily("os_info", "Operating system info", Arrays.asList("name", "arch", "version"));
        osInfo.addMetric(Arrays.asList(osBean.getName(), osBean.getArch(), osBean.getVersion()), 1L);
        mfs.add(osInfo);

        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            mfs.add(new GaugeMetricFamily("os_system_cpu_load", "The system CPU load as a number between 0 and 1",
                    sunOsBean.getSystemCpuLoad()));
            mfs.add(new GaugeMetricFamily("process_cpu_load", "The process CPU load as a number between 0 and 1",
                    sunOsBean.getProcessCpuLoad()));

            long totalMemory = sunOsBean.getTotalPhysicalMemorySize();
            long usedMemory = totalMemory - sunOsBean.getFreePhysicalMemorySize();
            mfs.add(new GaugeMetricFamily("os_memory_bytes_max", "Max available physical memory", totalMemory));
            mfs.add(new GaugeMetricFamily("os_memory_bytes_used", "Used physical memory", usedMemory));

            long totalSwap = sunOsBean.getTotalSwapSpaceSize();
            long usedSwap = totalSwap - sunOsBean.getFreeSwapSpaceSize();
            mfs.add(new GaugeMetricFamily("os_swap_bytes_max", "Max available swap space", totalSwap));
            mfs.add(new GaugeMetricFamily("os_swap_bytes_used", "Used swap space", usedSwap));

        }

        long availDiskSpace = 0;
        long totalDiskSpace = 0;

        try {
            for (FileStore store : defaultFs.getFileStores()) {
                availDiskSpace += store.getUsableSpace();
                totalDiskSpace += store.getTotalSpace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mfs.add(new GaugeMetricFamily("disk_space_bytes_used", "The disk space used in bytes for the default filesystem",
                totalDiskSpace - availDiskSpace));
        mfs.add(new GaugeMetricFamily("disk_space_bytes_max", "The total disk space in bytes for the default filesystem",
                totalDiskSpace));

        return mfs;
    }
}
