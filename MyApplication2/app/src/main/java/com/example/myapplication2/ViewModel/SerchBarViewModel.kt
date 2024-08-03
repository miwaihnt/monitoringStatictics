package com.example.myapplication2.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.Data.AllUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject




//例 pokemonUI用
//@HiltViewModel
//class SerchBarViewModel @Inject constructor(): ViewModel() {
//    private val uiState = MutableStateFlow(UiState())
//    val _uiState = uiState.asStateFlow()
//
//
//    fun onEvent(event: SearchBarEvent) {
//        when(event)  {
//            is SearchBarEvent.QueryChange -> {
//                var isQuerying = false
//                val pokemonList = mutableListOf<Pokemon>()
//                viewModelScope.launch {
//                    if(event.query.isNotEmpty()){
//                        isQuerying = true
//                        pokemonList.addAll(
//                            _pokemonList.filter {
//                                it.name.startsWith(
//                                    prefix = event.query,
//                                    ignoreCase = true
//                                )
//                            }
//                        )
//                    }
//                    else {
//                        pokemonList.addAll(_pokemonList)
//                    }
//                    uiState.value = _uiState.value.copy(
//                            query = event.query,
//                            isQuerying = isQuerying,
//                            pokemonList = pokemonList
//                        )
//                    Log.d("onEvent","query:${event.query}")
//                }
//            }
//            is SearchBarEvent.Select -> {
//                event.pokemon.let {
//                    _history.remove(it)
//                    _history.add(index = 0, it)
//                }
//
//                uiState.value =
//                    _uiState.value.copy(
//                        query = event.pokemon.name,
//                        isQuerying = true,
//                        selected = event.pokemon,
//                        searchHistory = _history,
//                        pokemonList = listOf(event.pokemon)
//                    )
//            }
//
//            is SearchBarEvent.Back -> {
//                uiState.value =
//                    _uiState.value.copy(
//                        selected = null
//                    )
//            }
//            is SearchBarEvent.Cancel -> {
//                uiState.value =
//                    _uiState.value.copy(
//                        query = "",
//                        isQuerying = false,
//                        pokemonList= _pokemonList,
//                        selected= null,
//                        searchHistory = _history
//                    )
//            }
//        }
//    }
//}
//
//
//
//
//
//sealed class SearchBarEvent{
//    data class QueryChange(val query: String): SearchBarEvent()
//    data class Select(val pokemon: Pokemon): SearchBarEvent()
//    object Back : SearchBarEvent()
//    object Cancel : SearchBarEvent()
//}
//
//
//data class UiState(
//    val query: String = "",
//    val isQuerying: Boolean = false,
//    val pokemonList:List<Pokemon> = _pokemonList,
//    val selected: Pokemon? = null,
//    val searchHistory: List<Pokemon> = _history
//)
//
//data class Pokemon(
//    val no: Int = 0,
//    val name: String,
//    val imageUrl: String,
//)
//
//private val _history = mutableListOf<Pokemon>()
//private val _pokemonList = listOf(
//    Pokemon(
//        1,
//        "フシギダネ",
//        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/001.png",
//    ),
//
//    Pokemon(
//            2,
//    "フシギソウ",
//        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/002.png",
//    ),
//    Pokemon(
//        3,
//        "フシギバナ",
//        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/003.png",
//    ),
//    Pokemon(
//            4,
//    "ヒトカゲ",
//        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/004.png",
//    ),
//    Pokemon(
//        7,
//        "ゼニガメ",
//        "https://assets.pokemon.com/assets/cms2/img/pokedex/full/007.png",
//        )
//
//)