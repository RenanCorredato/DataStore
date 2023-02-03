package com.renancorredato.datastore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.renancorredato.datastore.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnSalve.setOnClickListener {
            lifecycleScope.launch {
                salveDataToDataStore(
                    binding.edtKey.text.toString(),
                    binding.edtValue.text.toString()
                )
            }
        }

        binding.btnRead.setOnClickListener {
            lifecycleScope.launch {
//                binding.tvResult.text = getDataFromDataStore(binding.edtKey.text.toString())
                getDataFromDataStore(binding.edtKey.text.toString()).collect { //coletando os daos
                    binding.tvResult.text = it
                }
            }
        }

        binding.btnClear.setOnClickListener {
            lifecycleScope.launch {
                removeDataFromDataStore(binding.edtKey.text.toString())
            }
        }

        binding.btnClearAll.setOnClickListener {
            lifecycleScope.launch {
                removeAllDataFromDataStore()
            }
        }
    }

    private suspend fun removeAllDataFromDataStore() {
        dataStore.edit { settings ->
            settings.clear()
        }
    }

    private suspend fun removeDataFromDataStore(key: String) {
        val preferencesKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings.remove(preferencesKey)
        }
    }

// metodo simples
//    private suspend fun getDataFromDataStore(key: String): String {
//        val preferencesKey = stringPreferencesKey(key)
//        return dataStore.data.first()[preferencesKey] ?:"Valor não definido"
//    }


    //mais recomendado Flow
    private fun getDataFromDataStore(key: String): Flow<String> {
        val preferencesKey = stringPreferencesKey(key)
        val readValueFlow: Flow<String> = dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: "Valor não definido"
        }
        return readValueFlow
    }

    private suspend fun salveDataToDataStore(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        dataStore.edit { settings ->
            settings[preferencesKey] = value
        }
    }
}