package evaluator;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import oshi.json.SystemInfo;
import oshi.json.hardware.*;
import oshi.json.software.os.OperatingSystem;
import oshi.util.FormatUtil;

/**
 * Created by rohitpatiyal on 23/6/17.
 */
public class RequiredSpecs {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        // Options: ERROR > WARN > INFO > DEBUG > TRACE
        Logger LOG = LoggerFactory.getLogger(evaluator.HardwareEvaluator.class);

        LOG.info("Initializing System...");
        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        String fingerPrint = "";
        LOG.info("Checking computer system...");
        fingerPrint += printComputerSystem(hal.getComputerSystem());

        LOG.info("Checking Processor...");
        fingerPrint += printProcessor(hal.getProcessor());

        LOG.info("Checking Memory...");
        fingerPrint += printMemory(hal.getMemory());

        LOG.info("Checking Disks...");
        fingerPrint += printDisks(hal.getDiskStores());
        System.out.println(fingerPrint);
    }

    private static String printComputerSystem(final ComputerSystem computerSystem) {
        String cs = ""
        + "manufacturer: " + computerSystem.getManufacturer()
        + "model: " + computerSystem.getModel()
        + "serialnumber: " + computerSystem.getSerialNumber();
        final Firmware firmware = computerSystem.getFirmware();
        cs = cs +  "firmware:"
        + "  manufacturer: " + firmware.getManufacturer()
        + "  name: " + firmware.getName()
        + "  description: " + firmware.getDescription()
        + "  version: " + firmware.getVersion()
        + "  release date: " + (firmware.getReleaseDate() == null ? "unknown"
                : firmware.getReleaseDate() == null ? "unknown" : FormatUtil.formatDate(firmware.getReleaseDate()));
        final Baseboard baseboard = computerSystem.getBaseboard();
        cs = cs + "baseboard:"
        + "  manufacturer: " + baseboard.getManufacturer()
        + "  model: " + baseboard.getModel()
        + "  version: " + baseboard.getVersion()
        + "  serialnumber: " + baseboard.getSerialNumber();
        return  cs;
    }

    private static String printProcessor(CentralProcessor processor) {
        String p = ""
        + processor
        + " " + processor.getPhysicalProcessorCount() + " physical CPU(s)"
        + " " + processor.getLogicalProcessorCount() + " logical CPU(s)"
        + "Identifier: " + processor.getIdentifier()
        + "ProcessorID: " + processor.getProcessorID();
        return p;
    }

    private static String printMemory(GlobalMemory memory) {
        String m =  "Memory: " + FormatUtil.formatBytes(memory.getTotal())
        + "Swap : " + FormatUtil.formatBytes(memory.getSwapTotal());
        return m;
    }

    private static String printDisks(HWDiskStore[] diskStores) {
        String d =  "Disks:";
        for (HWDiskStore disk : diskStores) {
            boolean readwrite = disk.getReads() > 0 || disk.getWrites() > 0;
            d = d + String.format(" %s: (model: %s - S/N: %s) size: %s%n",
                    disk.getName(), disk.getModel(), disk.getSerial(),
                    disk.getSize() > 0 ? FormatUtil.formatBytesDecimal(disk.getSize()) : "?");
            HWPartition[] partitions = disk.getPartitions();
            if (partitions == null) {
                // TODO Remove when all OS's implemented
                continue;
            }
        }

        return d;
    }


}
