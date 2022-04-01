package com.ke.jdaddresspicker.demo

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ke.jdaddresspicker.lib.AddressPickerBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.show).setOnClickListener {
            show(it)
        }

    }

    fun show(view: View) {
        val json = assets.open("city_json.json").bufferedReader().readText()


        val gson = Gson()

        val type = object : TypeToken<List<CityBean>>() {}.type

        val list = gson.fromJson<List<CityBean>>(json, type)

        val provinceList = list

        val cityList = list.flatMap {
            it.children ?: emptyList()
        }

        val townList = cityList.flatMap {
            it.children ?: emptyList()
        }


        AddressPickerBuilder(this) { id ->
            if (id == null) {
                return@AddressPickerBuilder list.map {
                    it.value.toInt() to it.label
                }
            }
            val province = list.find { it.value.toInt() == id }

            if (province != null) {
                //点击了省 获取省下面的市
                return@AddressPickerBuilder cityList.filter {
                    it.parentId.toInt() == id
                }.map {
                    it.value.toInt() to it.label
                }
            }
            val city = townList.find { it.parentId.toInt() == id }

            if (city != null) {
                //点击了市 获取市下面的县
                return@AddressPickerBuilder townList.filter {
                    it.parentId.toInt() == id
                }.map {
                    it.value.toInt() to it.label
                }
            }



            emptyList()

        }.build().apply {
            onSelectedListener = { province, city, town ->
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("选择结果")
                    .setMessage("$province $city $town")
                    .show()
            }
            show()
        }

//        AlertDialog.Builder(this)
//            .show()
    }


}