package company.vk.lection09

import company.vk.base.films.businesslayer.FilmProvider
import company.vk.base.films.businesslayer.IFilmProvider
import company.vk.base.films.datalayer.IApiAccessor
import company.vk.base.loggers.Logger
import company.vk.base.videos.businesslayer.FilmManager
import company.vk.base.videos.presentationlayer.FilmViewModel
import company.vk.common.ServiceLocator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MockkSampleUnitTest {
	// good link - https://notwoods.github.io/mockk-guidebook/

	@BeforeClass
	fun setup() {

	}

	@Before
	fun prepare() {
		val mockLogger = mockk<Logger.ILogger>(relaxUnitFun = true)

		Logger.initialize(mockLogger)
	}

	@After
	fun clean() {

	}


	@Test
	fun mockk_test_must_fail() {
		ServiceLocator.inject(FilmViewModel::class.java)
	}

	@Test
	fun mockk_test_must_success() {
		val mockAccessor = mockk<IApiAccessor>(relaxUnitFun = true)
		every { runBlocking { mockAccessor.filmList()  } } returns emptyList()

		val mockkFactory = mockk<ServiceLocator.IFactory>(relaxUnitFun = true)
		every { mockkFactory.create(FilmManager::class.java) } returns FilmManager()
		every { mockkFactory.create(IFilmProvider::class.java) } returns FilmProvider()
		every { mockkFactory.create(IApiAccessor::class.java) } returns mockAccessor
		ServiceLocator.addFactory(mockkFactory)

		val model = FilmViewModel()
		model.load()

		verify { mockkFactory.create(FilmManager::class.java) }
		verify { mockkFactory.create(IFilmProvider::class.java) }
		verify { mockkFactory.create(IApiAccessor::class.java) }

		verify { runBlocking { mockAccessor.filmList()  } }
		// or
		verify(exactly = 1) { runBlocking { mockAccessor.filmList() } }
	}
}