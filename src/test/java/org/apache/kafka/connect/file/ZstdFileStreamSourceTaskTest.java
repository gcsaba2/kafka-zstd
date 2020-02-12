package org.apache.kafka.connect.file;

import com.github.luben.zstd.Zstd;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(BlockJUnit4ClassRunner.class)
public class ZstdFileStreamSourceTaskTest {

    @Test
    public void testConnect() throws Exception {
        final String rawStr = "Alszik az erdo\n" +
                "Alszik a mezo\n" +
                "Alszik a hinta\n" +
                "Alszanak a fak\n";

        File tmpFile = compressToFile(rawStr);
        ZstdFileStreamSourceTask task = initSourceTask(tmpFile);

        List<SourceRecord> result = task.poll();

        assertNotNull(result);
        assertEquals(4, result.size());
    }

    private File compressToFile(String rawStr) throws Exception {
        File tmpFile = File.createTempFile("zstd", "txt");
        try (FileOutputStream out = new FileOutputStream(tmpFile)) {
            byte[] compressedStr = Zstd.compress(rawStr.getBytes());
            out.write(compressedStr);
            out.flush();
        }
        return tmpFile;
    }

    private ZstdFileStreamSourceTask initSourceTask(File tmpFile) {
        Map<String, String> config = new HashMap<>();
        config.put(ZstdFileStreamSourceConnector.FILE_CONFIG, tmpFile.getAbsolutePath());
        config.put(ZstdFileStreamSourceConnector.TOPIC_CONFIG, "test");
        config.put(ZstdFileStreamSourceConnector.TASK_BATCH_SIZE_CONFIG, "100");

        SourceTaskContext context = mock(SourceTaskContext.class);
        OffsetStorageReader mockOffset = mock(OffsetStorageReader.class);
        when(context.offsetStorageReader()).thenReturn(mockOffset);
        when(mockOffset.offset(any())).thenReturn(null);

        ZstdFileStreamSourceTask task = new ZstdFileStreamSourceTask();
        task.initialize(context);
        task.start(config);
        return task;
    }

}
