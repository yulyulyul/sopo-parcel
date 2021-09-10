package team.sopo.common.logging.filter

import java.io.IOException
import org.springframework.stereotype.Component
import javax.servlet.*
import javax.servlet.http.HttpServletRequest

@Component
class ReadableRequestWrapperFilter : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val wrapper = ReadableRequestWrapper(request as HttpServletRequest)
        chain.doFilter(wrapper, response)
    }

}