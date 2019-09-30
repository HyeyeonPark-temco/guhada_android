package io.temco.guhada.common.util

import android.util.Log

/**
 * 안드로이드 내부 로그 출력
 *
 * @author park jungho
 */
object CustomLog {

    var num = 0
    var err = 0

    @JvmStatic
    val flag = false

    val maxLogStringSize = 1000

    @JvmStatic
    fun E(e: Exception) {
        try {
            if (flag) {
                Log.e(
                        "Exception",
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
                )
                if (flag) e.printStackTrace()
                Log.e(
                        "Exception",
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
                )
            }
        } catch (e2: Exception) {
            if (flag) e2.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: String, msg: String) {
        try {
            if (flag) {
                try {
                    for (i in 0..msg.length / maxLogStringSize) {
                        val start = i * maxLogStringSize
                        var end = (i + 1) * maxLogStringSize
                        end = if (end > msg.length) msg.length else end
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "]  " +
                                        msg.substring(start, end)
                        )
                    }
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }


    @JvmStatic
    fun L(tag: String, vararg args: Any) {
        try {
            if (flag) {
                try {
                    var msg = ""
                    args.forEach {
                        if(!"".equals(msg)) msg += " "
                        msg += it.toString()
                    }
                    for (i in 0..msg.length / maxLogStringSize) {
                        val start = i * maxLogStringSize
                        var end = (i + 1) * maxLogStringSize
                        end = if (end > msg.length) msg.length else end
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "]  " +
                                        msg.substring(start, end)
                        )
                    }
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }


    @JvmStatic
    fun L(tag: String, msg: Any?) {
        try {
            if (flag) {
                try {
                    if (msg is Int) {
                        Log.d(
                                tag,
                                "[" + String.format("%25s", tag) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "Integer value " + msg!!.toString()
                        )
                    } else if (msg is Long) {
                        Log.d(
                                tag,
                                "[" + String.format("%25s", tag) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "Long value " + msg!!.toString()
                        )
                    } else if (msg is Float) {
                        Log.d(
                                tag,
                                "[" + String.format("%25s", tag) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "Float value " + msg!!.toString()
                        )
                    } else if (msg is Double) {
                        Log.d(
                                tag,
                                "[" + String.format("%25s", tag) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "Double value " + msg!!.toString()
                        )
                    } else if (msg is HashMap<*,*>) {
                        Log.d(
                                tag,
                                "[" + String.format("%25s", tag) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "HashMap value " + msg!!.toString()
                        )
                    } else {
                        Log.d(
                                tag,
                                "[" + String.format("%25s", tag) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + if (msg == null) "value Null" else msg!!.toString()
                        )
                    }
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: String, msg: Array<String>) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag,
                            "[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n"
                    )

                    for (i in msg.indices) {
                        val m = if (msg[i] != null) msg[i] else "-null-"
                        Log.d(
                                tag,
                                tag + "[ Array " + String.format("%12s", i) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + m + "\n"
                        )
                    }
                    Log.d(
                            tag, "[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "] End " +
                            "---------------------------------------------------------------------------------------------- " + "\n"
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: String, msg: DoubleArray) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag,
                            "[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n"
                    )

                    for (i in msg.indices) {
                        val m = (msg[i]).toString() + ""
                        Log.d(
                                tag,
                                "[ StringArray " + String.format("%12s", i) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + m + "\n"
                        )
                    }
                    Log.d(
                            tag, "[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "] End " +
                            "---------------------------------------------------------------------------------------------- " + "\n"
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: String, msg: ArrayList<HashMap<String, String>>, key: Array<String>) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag,
                            "[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n"
                    )
                    for (i in 0 until msg.size) {
                        for (j in key.indices) {
                            var c = ""
                            if (msg[i].containsKey(key[j])) {
                                c = "KeyValue : " + key[j] + " , Value : " + msg[i][key[j]]
                            }

                            Log.d(
                                    tag,
                                    "[ ArrayList (" + String.format("%12s", i) + ")]" + "  [" + String.format(
                                            "%04d",
                                            num++
                                    ) + "]  " + c + "\n"
                            )
                        }
                        Log.d(
                                tag,
                                "-------------------------------------------------------------------------------------------" +
                                        "----------------------------------------------------------------------------- " + "\n"
                        )
                    }
                    Log.d(
                            tag.javaClass.canonicalName,
                            "[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] End " +
                                    "---------------------------------------------------------------------------------------------- " + "\n"
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: String, msg: HashMap<String, String>, key: Array<String>) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag,
                            ("[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n")
                    )
                    for (j in key.indices) {
                        var c = ""
                        if (msg.containsKey(key[j])) {
                            c = "KeyValue : " + key[j] + " , Value : " + msg[key[j]]
                            /*if(msg.get(key[j]) != null && msg.get(key[j]).length() > 0){
                                                           c = "KeyValue : "+key[j] +" , Value : "+ msg.get(key[j]);
                                                       }else{
                                                           c ="null";
                                                       }*/
                        }
                        Log.d(
                                tag,
                                "[ ArrayList (" + String.format("%12s", j) + ")]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + c + "\n"
                        )
                    }
                    Log.d(
                            tag,
                            ("-------------------------------------------------------------------------------------------" +
                                    "----------------------------------------------------------------------------- " + "\n")
                    )

                    Log.d(
                            tag, ("[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "] End " +
                            "---------------------------------------------------------------------------------------------- " + "\n")
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: String, msg: ArrayList<HashMap<String, String>>) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag,
                            ("[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n")
                    )
                    for (i in 0 until msg.size) {
                        Log.d(
                                tag,
                                " ArrayList [" + String.format("%12s", i) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + msg[i].toString()
                        )
                    }
                    Log.d(
                            tag, ("[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "] End " +
                            "---------------------------------------------------------------------------------------------- " + "\n")
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: Any, msg: String) {
        try {
            if (flag) {
                try {
                    for (i in 0..msg.length / maxLogStringSize) {
                        val start = i * maxLogStringSize
                        var end = (i + 1) * maxLogStringSize
                        end = if (end > msg.length) msg.length else end
                        Log.d(
                                tag.javaClass.canonicalName,
                                ("[" + String.format("%25s", tag) + "]" + "  [" + String.format("%04d", num++) + "]  " +
                                        msg.substring(start, end))
                        )
                    }
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }


    @JvmStatic
    fun L(tag: Any, msg: Any) {
        try {
            if (flag) {
                try {
                    if (msg is Int) {
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "Integer value " + msg.toString()
                        )
                    } else if (msg is Long) {
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "Long value " + msg.toString()
                        )
                    } else if (msg is Float) {
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "Float value " + msg.toString()
                        )
                    } else if (msg is Double) {
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "Double value " + msg.toString()
                        )
                    } else if (msg is HashMap<*,*>) {
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + "HashMap value " + msg.toString()
                        )
                    }
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: Any, msg: Array<String>) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n")
                    )

                    for (i in msg.indices) {
                        val m = (if (msg[i] != null) msg[i] else "-null-")
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[ StringArray " + String.format("%12s", i) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + m + "\n"
                        )
                    }
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] End " +
                                    "---------------------------------------------------------------------------------------------- " + "\n")
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: Any, msg: DoubleArray) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n")
                    )

                    for (i in msg.indices) {
                        val m = (msg[i]).toString() + ""
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[ StringArray " + String.format("%12s", i) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + m + "\n"
                        )
                    }
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] End " +
                                    "---------------------------------------------------------------------------------------------- " + "\n")
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: Any, msg: ArrayList<HashMap<String, String>>, key: Array<String>) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n")
                    )
                    for (i in 0 until msg.size) {
                        for (j in key.indices) {
                            var c = ""
                            if (msg[i].containsKey(key[j])) {
                                c = "KeyValue : " + key[j] + " , Value : " + msg[i][key[j]]
                                /*if(msg.get(i).get(key[j]) != null && msg.get(i).get(key[j]).length() > 0){
                                                                   c = "KeyValue : "+key[j] +" , Value : "+ msg.get(i).get(key[j]);
                                                               }else{
                                                                   c ="null";
                                                               }*/
                            }

                            Log.d(
                                    tag.javaClass.canonicalName,
                                    "[ ArrayList (" + String.format("%12s", i) + ")]" + "  [" + String.format(
                                            "%04d",
                                            num++
                                    ) + "]  " + c + "\n"
                            )
                        }
                        Log.d(
                                tag.javaClass.canonicalName,
                                ("-------------------------------------------------------------------------------------------" +
                                        "----------------------------------------------------------------------------- " + "\n")
                        )
                    }
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] End " +
                                    "---------------------------------------------------------------------------------------------- " + "\n")
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: Any, msg: HashMap<String, String>, key: Array<String>) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n")
                    )
                    for (j in key.indices) {
                        var c = ""
                        if (msg.containsKey(key[j])) {
                            c = "KeyValue : " + key[j] + " , Value : " + msg[key[j]]
                            /*if(msg.get(key[j]) != null && msg.get(key[j]).length() > 0){
                                                           c = "KeyValue : "+key[j] +" , Value : "+ msg.get(key[j]);
                                                       }else{
                                                           c ="null";
                                                       }*/
                        }
                        Log.d(
                                tag.javaClass.canonicalName,
                                "[ ArrayList (" + String.format("%12s", j) + ")]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + c + "\n"
                        )
                    }
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("-------------------------------------------------------------------------------------------" +
                                    "----------------------------------------------------------------------------- " + "\n")
                    )

                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] End " +
                                    "---------------------------------------------------------------------------------------------- " + "\n")
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun L(tag: Any, msg: ArrayList<HashMap<String, String>>) {
        try {
            if (flag) {
                try {
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] Start " +
                                    "-------------------------------------------------------------------------------------------- " + "\n")
                    )
                    for (i in 0 until msg.size) {
                        Log.d(
                                tag.javaClass.canonicalName,
                                " ArrayList [" + String.format("%12s", i) + "]" + "  [" + String.format(
                                        "%04d",
                                        num++
                                ) + "]  " + msg[i].toString()
                        )
                    }
                    Log.d(
                            tag.javaClass.canonicalName,
                            ("[" + String.format("%25s", tag.javaClass.simpleName) + "]" + "  [" + String.format(
                                    "%04d",
                                    num++
                            ) + "] End " +
                                    "---------------------------------------------------------------------------------------------- " + "\n")
                    )
                } catch (e: Exception) {
                    E(e)
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }

    @JvmStatic
    fun E(msg: String) {
        try {
            if (flag) {
                try {
                    num++
                    Log.e("Execpion", "[" + String.format("%04d", err++) + "]  " + msg)
                } catch (e: Exception) {
                    if (flag) e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }


    @JvmStatic
    fun E(msg: Throwable) {
        try {
            if (flag) {
                try {
                    num++
                    Log.e("Execpion", "[" + String.format("%04d", err++) + "]  " + msg.printStackTrace())
                } catch (e: Exception) {
                    if (flag) e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            if (flag) e.printStackTrace()
        }

    }
}