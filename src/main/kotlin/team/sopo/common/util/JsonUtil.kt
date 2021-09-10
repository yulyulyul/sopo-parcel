package team.sopo.common.util

import net.minidev.json.JSONObject
import org.codehaus.jettison.json.JSONException

object JsonUtil {

    fun mapToJson(map: Map<String, Any>): String{
        val jsonObj = JSONObject()
        try{

            for(entry :Map.Entry<String, Any> in map.entries){
                val key = entry.key
                val value = entry.value
                jsonObj[key] = value
            }
        }
        catch (e: JSONException){
            e.printStackTrace()
        }
        return jsonObj.toJSONString()
    }
}