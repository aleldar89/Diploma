package ru.netology.diploma.adapter

import com.google.gson.Gson
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.ToponymObjectMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.netology.diploma.dto.Coordinates
import java.util.concurrent.TimeUnit

class UserLocation {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    companion object {
        private const val BASE_URL =
            "https://geocode-maps.yandex.ru/1.x/?apikey=e35ce02e-18b3-4678-9859-286826ff3245&geocode="
    }

    suspend fun getAddress(coords: Coordinates): String? {
        val request: Request = Request.Builder()
            .url(
                "$BASE_URL${coords.longitude},${coords.lat}&kind=locality&format=json&results=1"
            )
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request)
                .execute()
                .let {
                    it.body?.string() ?: throw RuntimeException("body is null")
                }
                .let {
                    gson.fromJson(it, GeoObjectCollection::class.java)
                }
                .let {
                    it?.children?.firstOrNull()?.obj
                        ?.metadataContainer
                        ?.getItem(ToponymObjectMetadata::class.java)
                        ?.address
                        ?.components
                        ?.firstOrNull {
                            it.kinds.contains(Address.Component.Kind.LOCALITY)
                        }
                        ?.name
                }
        }
    }

}