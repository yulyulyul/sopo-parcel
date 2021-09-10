package team.sopo.common.logging.filter

import com.google.common.io.ByteStreams.toByteArray
import org.apache.commons.lang3.StringUtils
import javax.servlet.http.HttpServletRequest
import org.apache.http.entity.ContentType
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import javax.servlet.ServletInputStream
import java.io.ByteArrayInputStream
import javax.servlet.ReadListener
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequestWrapper
import org.apache.logging.log4j.LogManager

class ReadableRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val encoding: Charset
    private lateinit var rawData: ByteArray
    private val params: MutableMap<String, Array<String>> = HashMap()
    private val logger = LogManager.getLogger(this.javaClass)

    override fun getParameter(name: String): String? {
        val paramArray = getParameterValues(name)
        return if (paramArray != null && paramArray.isNotEmpty()) {
            paramArray[0]
        } else {
            null
        }
    }

    override fun getParameterMap(): Map<String, Array<String>> {
        return Collections.unmodifiableMap(params)
    }

    override fun getParameterNames(): Enumeration<String> {
        return Collections.enumeration(params.keys)
    }

    override fun getParameterValues(name: String): Array<String?>? {
        var result: Array<String?>? = null
        val dummyParamValue = params[name]
        if (dummyParamValue != null) {
            result = arrayOfNulls(dummyParamValue.size)
            System.arraycopy(dummyParamValue, 0, result, 0, dummyParamValue.size)
        }
        return result
    }

    fun setParameter(name: String, value: String) {
        val param = arrayOf(value)
        setParameter(name, param)
    }

    fun setParameter(name: String, values: Array<String>) {
        params[name] = values
    }

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(rawData)
        return object : ServletInputStream() {
            override fun isFinished(): Boolean {
                return false
            }

            override fun isReady(): Boolean {
                return false
            }

            override fun setReadListener(readListener: ReadListener) {
                // Do nothing
            }

            override fun read(): Int {
                return byteArrayInputStream.read()
            }
        }
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(this.inputStream, encoding))
    }

    init {
        params.putAll(request.parameterMap) // 원래의 파라미터를 저장
        val charEncoding = request.characterEncoding // 인코딩 설정
        encoding = if (StringUtils.isBlank(charEncoding)) StandardCharsets.UTF_8 else Charset.forName(charEncoding)

        val inputStream: InputStream = request.inputStream
        rawData = toByteArray(inputStream) // InputStream 을 별도로 저장한 다음 getReader() 에서 새 스트림으로 생성

        // body 파싱
        val collect = this.reader.lines().collect(Collectors.joining(System.lineSeparator()))

        run {
            if (StringUtils.isEmpty(collect)) { // body 가 없을경우 로깅 제외
                return@run
            }

            if (request.contentType != null && request.contentType.contains(
                    ContentType.MULTIPART_FORM_DATA.mimeType
                )
            ) { // 파일 업로드시 로깅제외
                return@run
            }

            val jsonParser = JSONParser()
            try {
                val parse = jsonParser.parse(collect)
                if (parse is JSONArray) {
                    val jsonArray = jsonParser.parse(collect) as JSONArray
                    setParameter("requestBody", jsonArray.toJSONString())
                } else {
                    val jsonObject = jsonParser.parse(collect) as JSONObject
                    val iterator: Iterator<*> = jsonObject.keys.iterator()
                    while (iterator.hasNext()) {
                        val key = iterator.next() as String
                        setParameter(key, jsonObject[key].toString().replace("\"", "\\\""))
                    }
                }
            }catch (e: ParseException) {
                logger.info("ReadableRequestWrapper json parser error$e")
            }
        }

    }
}