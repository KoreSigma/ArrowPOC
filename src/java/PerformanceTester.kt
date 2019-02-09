import test.ReadWrite
import test.Serialize

class PerformanceTester<TData>(
    private val serializeServices: List<ServiceInfo<Serialize<TData>>>,
    private val readWriteServices: List<ServiceInfo<ReadWrite>>
) {

    class ServiceInfo<TService>(
        val service: TService,
        val platform: String,
        val format: String
    )

    class DataInfo(
        val data: Any?,
        val name: String
    )

    class TestResult(
        fromPlatform: String,
        toPlatform: String,
        serialFormat: String,
        readWriteFormat: String,
        serializeDuration: Long,
        writeDuration: Long,
        readDuration: Long,
        deserilizeDuration: Long
    )

    fun test(data: TData): List<TestResult> {
        val result = mutableListOf<TestResult>()

        for (serializer in serializeServices) {
            for (writer in readWriteServices) {
                if (writer.platform != serializer.platform) continue
                for (reader in readWriteServices) {
                    if (reader.format != writer.format) continue
                    for (deserializer in serializeServices) {
                        if (deserializer.platform != reader.platform) continue
                        if (deserializer.format != serializer.format) continue

                        result += test(
                            data,
                            serializer,
                            writer,
                            reader,
                            deserializer
                        )
                    }
                }
            }
        }

        return result
    }

    private fun test(
        data: TData,
        serializer: ServiceInfo<Serialize<TData>>,
        writer: ServiceInfo<ReadWrite>,
        reader: ServiceInfo<ReadWrite>,
        deserializer: ServiceInfo<Serialize<TData>>
    ): TestResult {
        lateinit var serialized: ByteArray
        lateinit var read: ByteArray

        return TestResult(
            fromPlatform = serializer.platform,
            toPlatform = deserializer.platform,
            serialFormat = serializer.format,
            readWriteFormat = reader.format,
            serializeDuration = time { serialized = serializer.service.serialize(data) },
            writeDuration = time { writer.service.write(serialized) },
            readDuration = time { read = reader.service.read() },
            deserilizeDuration = time { deserializer.service.deserialize(read) }
        )
    }

    private inline fun time(action: () -> Unit): Long {
        val start = System.nanoTime()
        action()
        return System.nanoTime() - start
    }
}