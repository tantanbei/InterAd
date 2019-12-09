package com.hytt.activation.trace

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object Trace {
    fun SendTrace(type: String, op1: String, op2: String, ext: String) {
        Thread(Runnable {
            kotlin.run {
                var conn: HttpURLConnection? = null
                var reader: BufferedReader? = null
                try {
                    val url = URL("https://rcv.hyrainbow.com/trace?t=" + type + "&op1=" + op1 + "&op2=" + op2 + ext)
                    Log.d("tan", "https://rcv.hyrainbow.com/trace?t=" + type + "&op1=" + op1 + "&op2=" + op2 + ext)

                    conn = url.openConnection() as HttpURLConnection?
                    //设置请求方法
                    conn?.setRequestMethod("GET")
                    //设置连接超时时间（毫秒）
                    conn?.setConnectTimeout(5000)
                    //设置读取超时时间（毫秒）
                    conn?.setReadTimeout(5000)

                    //返回输入流
                    val input = conn?.getInputStream()

                    //读取输入流
                    reader = BufferedReader(InputStreamReader(input))
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    if (reader != null) {
                        try {
                            reader.close()
                        } catch (e: IOException) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {//关闭连接
                        conn.disconnect()
                    }
                }
            }
        }).start()
    }
}