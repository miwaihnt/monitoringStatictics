package com.example.myapplication2.MainViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapplication2.remote.data.source.PokemonEntry
import com.example.myapplication2.remote.data.source.PokemonSpecies
import com.example.myapplication2.repository.Pokedex
import com.example.myapplication2.repository.PokemonRepository
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` // もしくは import org.mockito.Mockito.whenever
import org.mockito.Mockito.`when` as whenever




@ExperimentalCoroutinesApi
@HiltAndroidTest
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Mock
    private lateinit var pokemonRepository: PokemonRepository

    private lateinit var viewModel: MainViewModel
    private lateinit var testScope: TestScope
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)
        testScope = TestScope(testDispatcher)
        viewModel = MainViewModel(pokemonRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitalUiState() {
        assertEquals(viewModel.uiState.value,MainViewModel.UiState.Loading)
    }

    @Test
    fun testSearchQuery() {
        assertEquals(viewModel.searchQuery.value,""  )
    }


    // テスト用のダミーPokedexオブジェクトの生成
    private fun mockPokedex(): Pokedex {
        // 実際の実装に応じて適切なダミーオブジェクトを生成する
        val pokemonList = listOf(
            PokemonEntry(
                entryNumber = 1,
                pokemonSpecies = PokemonSpecies(
                        name = "pikachu",
                        url = "https://pokeapi.co/api/v2/pokemon-species/1/"
                        )
            ),
            PokemonEntry(
                entryNumber = 2,
                pokemonSpecies = PokemonSpecies(
                    name = "nya-su",
                    url = "https://pokeapi.co/api/v2/pokemon-species/3/"
                )
            ),
        )

        return Pokedex(
            id = 1,
            name = "national",
            pokemonEntries = pokemonList
        )
    }

    @Test
    fun testOnViewCreated() = testScope.runTest {
        val expectedPokedex = mockPokedex()

        //モックを設定する
        whenever(pokemonRepository.getPokedex()).thenReturn(expectedPokedex)
        //onViewCreatedの呼び出し
        viewModel.onViewCreated()
        //onViewCreatedが終わるまでまつ
        advanceUntilIdle()
        //結果検証
        assertTrue(viewModel.uiState.value is MainViewModel.UiState.SuccessPokedex)
        assertEquals((viewModel.uiState.value as MainViewModel.UiState.SuccessPokedex).pokedex,expectedPokedex)
    }

}
