package com.joaquimley.transporteta.presentation.favorite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.joaquimley.transporteta.domain.interactor.favorites.ClearAllTransportsAsFavoriteUseCase
import com.joaquimley.transporteta.domain.interactor.favorites.GetFavoritesUseCase
import com.joaquimley.transporteta.domain.interactor.favorites.MarkTransportAsFavoriteUseCase
import com.joaquimley.transporteta.domain.interactor.favorites.MarkTransportAsNoFavoriteUseCase
import com.joaquimley.transporteta.domain.interactor.transport.CancelEtaRequestUseCase
import com.joaquimley.transporteta.domain.interactor.transport.RequestEtaUseCase
import com.joaquimley.transporteta.domain.model.Transport
import com.joaquimley.transporteta.presentation.data.Resource
import com.joaquimley.transporteta.presentation.data.ResourceState
import com.joaquimley.transporteta.presentation.home.favorite.FavoritesViewModelImpl
import com.joaquimley.transporteta.presentation.mapper.TransportMapper
import com.joaquimley.transporteta.presentation.model.TransportView
import com.joaquimley.transporteta.presentation.util.factory.DataFactory
import com.joaquimley.transporteta.presentation.util.factory.TransportFactory
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Flowable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val robot = Robot()

    private val mockGetFavoritesUseCase = mock<GetFavoritesUseCase>()
    private val mockMarkTransportAsFavoriteUseCase = mock<MarkTransportAsFavoriteUseCase>()
    private val mockMarkTransportAsNoFavoriteUseCase = mock<MarkTransportAsNoFavoriteUseCase>()
    private val mockClearAllTransportsAsFavoriteUseCase = mock<ClearAllTransportsAsFavoriteUseCase>()
    private val mockRequestEtaUseCase = mock<RequestEtaUseCase>()
    private val mockCancelEtaRequestUseCase = mock<CancelEtaRequestUseCase>()
    private val mockMapper = mock<TransportMapper>()

    private val favoritesMockObserver = mock<Observer<Resource<List<TransportView>>>>()

    private lateinit var viewModel: FavoritesViewModelImpl


    @Before
    fun setup() {
        robot.stubGetFavoritesUseCaseSuccess()

        viewModel = FavoritesViewModelImpl(mockGetFavoritesUseCase,
                mockMarkTransportAsFavoriteUseCase,
                mockMarkTransportAsNoFavoriteUseCase,
                mockClearAllTransportsAsFavoriteUseCase,
                mockRequestEtaUseCase,
                mockCancelEtaRequestUseCase,
                mockMapper)
    }

    @After
    fun tearDown() {
    }


    @Test
    fun favoritesDataIsFetchedAtStartup() {
        // Assemble
        robot.stubGetFavoritesUseCaseSuccess()
        // Act
        // -> No action
        // Assert
        verify(mockGetFavoritesUseCase, atLeastOnce()).execute(anyOrNull())
    }

    @Test
    fun getFavoritesExecutesGetFavoritesUseCase() {
        // Assemble
        robot.stubGetFavoritesUseCaseSuccess()
        // Act
        viewModel.getFavorites(true)
        // Assert
        verify(mockGetFavoritesUseCase, atLeast(2)).execute(anyOrNull())
    }

    @Test
    fun onRefreshTriggersNewDataFetch() {
        // Assemble
        robot.stubGetFavoritesUseCaseSuccess()
        // Act
        viewModel.onRefresh()
        // Assert
        verify(mockGetFavoritesUseCase, atLeast(2)).execute(anyOrNull())
    }

    @Test
    fun getFavoritesTriggersLoadingState() {
        // Assemble
        robot.stubGetFavoritesUseCaseSuccess()
        viewModel.getFavorites().observeForever(favoritesMockObserver)
        // Act
        viewModel.getFavorites(true) // Have to force new data load to trigger state
        // Assert
        verify(favoritesMockObserver).onChanged(Resource(ResourceState.LOADING))
    }

    @Test
    fun getFavoritesLoadingStateHasNoData() {
        // Assemble
        robot.stubGetFavoritesUseCaseSuccess()
        viewModel.getFavorites().observeForever(favoritesMockObserver)
        // Act
        viewModel.getFavorites(true) // Have to force new data load to trigger state
        // Assert
        verify(favoritesMockObserver).onChanged(Resource(ResourceState.LOADING, null))
    }

    @Test
    fun getFavoritesLoadingStateHasNoMessage() {
        // Assemble
        robot.stubGetFavoritesUseCaseSuccess()
        // Act
        viewModel.getFavorites().observeForever(favoritesMockObserver)
        // Assert
        verify(favoritesMockObserver).onChanged(Resource(ResourceState.LOADING, anyOrNull(), null))
    }

    @Test
    fun getFavoritesReturnsCorrectStateOnError() {
        // Assemble
        robot.stubGetFavoritesUseCaseError()
        // Act
        viewModel.getFavorites(true)
        // Assert
        assert(viewModel.getFavorites().value?.status == ResourceState.ERROR)
        // Fail reason
        { "\nStatus was ${viewModel.getFavorites().value?.status}\nInstead of: ERROR" }
    }

    @Test
    fun getFavoritesReturnsNoDataOnError() {
        // Assemble
        robot.stubGetFavoritesUseCaseError()
        // Act
        viewModel.getFavorites(true)
        // Assert
        assert(viewModel.getFavorites().value?.data == null)
        // Fail reason
        { "\nData was ${viewModel.getFavorites().value?.data}\nInstead of: null" }
    }

    @Test
    fun getFavoritesReturnsCorrectMessageOnError() {
        // Assemble
        val throwable = robot.stubGetFavoritesUseCaseError()
        // Act
        viewModel.getFavorites(true)
        // Assert
        assert(viewModel.getFavorites().value?.message.equals(throwable.message))
        // Fail reason
        { "\nMessage was ${viewModel.getFavorites().value?.message}\nInstead of: ${throwable.message}" }
    }

    @Test
    fun getFavoritesReturnsCorrectStateOnSuccess() {
        // Assemble
        robot.stubGetFavoritesUseCaseSuccess()
        // Act
        viewModel.getFavorites(true)
        // Assert
        assert(viewModel.getFavorites().value?.status == ResourceState.SUCCESS)
        // Fail reason
        { "\nStatus was ${viewModel.getFavorites().value?.status}\nInstead of: SUCCESS" }
    }

    @Test
    fun getFavoritesReturnsNoErrorMessageOnSuccess() {
        // Assemble
        robot.stubGetFavoritesUseCaseSuccess()
        // Act
        viewModel.getFavorites(true)
        // Assert
        assert(viewModel.getFavorites().value?.message == null)
        // Fail reason
        { "\nMessage was ${viewModel.getFavorites().value?.message}\nInstead of null" }
    }

    @Test
    fun getFavoritesReturnsCorrectDataOnSuccess() {
        // Assemble
        val useCaseData = TransportFactory.makeTransportList(5)
        val stubbedMapperDataList = TransportFactory.makeTransportViewList(5)
        robot.stubMapper(useCaseData, stubbedMapperDataList, true)
        // Act
        viewModel.getFavorites(true)
        // Assert
        assert(viewModel.getFavorites().value?.data == stubbedMapperDataList)
        // Fail reason
        { "\nData was:\n${viewModel.getFavorites().value?.data}\nInstead of:\n$stubbedMapperDataList" }
    }

    inner class Robot {

        // region Robot public API

        fun stubGetFavoritesUseCaseSuccess(flowable: List<Transport>? = null, count: Int = 3): List<Transport> {
            val testData = flowable ?: TransportFactory.makeTransportList(count)
            stubExecuteGetFavoritesUseCase(Flowable.just(testData))
            return testData
        }

        fun stubGetFavoritesUseCaseError(message: String? = DataFactory.randomString()): Throwable {
            val throwable = Throwable(message)
            stubExecuteGetFavoritesUseCase(Flowable.error(throwable))
            return throwable
        }

        fun stubMapper(transportList: List<Transport>, transportViewList: List<TransportView>, isStubGetFavoritesUseCase: Boolean = false) {
            stubTransportMapperToView(transportList, transportViewList)
            if (isStubGetFavoritesUseCase) {
                robot.stubGetFavoritesUseCaseSuccess(transportList)
            }
        }

        // endregion Robot public API

        // region Robot internal implementation
        private fun stubTransportMapperToView(transport: List<Transport>, transportView: List<TransportView>) {
            whenever(mockMapper.toView(transport)).then { transportView }
        }

        private fun stubExecuteGetFavoritesUseCase(flowable: Flowable<List<Transport>>) {
            whenever(mockGetFavoritesUseCase.execute(anyOrNull())).then { flowable }
        }

        // endregion Robot internal implementation
    }
}

//    @Test
//    fun `fetch eta triggers not accepting requests state`() {
//        // given
//        val favoriteView = TransportView(Random().nextInt(), UUID.randomUUID().toString(), UUID.randomUUID().toString())
//        // wMockitoAnnotations.initMocks(this)hen
//        favoritesViewModel.isAcceptingRequests().observeForever(requestStatusObserver)
//        favoritesViewModel.onEtaRequested(favoriteView)
//        // then
//        verify(requestStatusObserver).onChanged(false)
//    }
//
//    @Test
//    @Throws(IllegalArgumentException::class)
//    fun `when favorite eta request is canceled accepting requests is true`() {
//        // given
//        val favoriteView = TransportView(Random().nextInt(), UUID.randomUUID().toString(), UUID.randomUUID().toString())
//        favoritesViewModel.onEtaRequested(favoriteView)
//        // when
//        favoritesViewModel.isAcceptingRequests().observeForever(requestStatusObserver)
//        favoritesViewModel.onEtaRequestCanceled()
//        // then
//        verify(requestStatusObserver).onChanged(true)
//    }
//
//    @Test
//    @Throws(IllegalArgumentException::class)
//    fun `when favorite eta request is canceled smsController invalidate request is called`() {
//        // given
//        val favoriteView = TransportView(Random().nextInt(), UUID.randomUUID().toString(), UUID.randomUUID().toString())
//        favoritesViewModel.onEtaRequested(favoriteView)
//        // when
//        favoritesViewModel.onEtaRequestCanceled()
//        // then
//        verify(smsController, times(1)).invalidateRequest()
//    }
//
//    @Test
//    @Throws(IllegalArgumentException::class)
//    fun `when favorite eta is requested smsController requestEta is called`() {
//        // given
//        val favoriteView = TransportView(Random().nextInt(), UUID.randomUUID().toString(), UUID.randomUUID().toString())
//        // when
//        favoritesViewModel.onEtaRequested(favoriteView)
//        // then
//        verify(smsController, times(1)).requestEta(anyInt())
//    }
//
//    @Test
//    @Throws(IllegalArgumentException::class)
//    fun `when favorite eta is requested correct favorite code is passed to smsController`() {
//        // given
//        val favoriteView = TransportView(Random().nextInt(), UUID.randomUUID().toString(), UUID.randomUUID().toString())
//        // when
//        favoritesViewModel.onEtaRequested(favoriteView)
//        verify(smsController).requestEta(captor.capture())
//        // then
//        assertThat(captor.firstValue, `is`(favoriteView.code))
//    }
//
//    @Test
//    @Throws(IllegalArgumentException::class)
//    fun `when sms is received triggers accepting requests state`() {
//        // given
//        val favoriteView = TransportView(Random().nextInt(), UUID.randomUUID().toString(), UUID.randomUUID().toString())
//        favoritesViewModel.onEtaRequested(favoriteView)
//        // when
//        favoritesViewModel.isAcceptingRequests().observeForever(requestStatusObserver)
//        smsResult.onNext(TestModelsFactory.generateSmsModel())
//        // then
//        verify(requestStatusObserver).onChanged(true)
//    }
//
//    @Test
//    @Throws(IllegalArgumentException::class)
//    fun `when sms is received correct data is passed`() {
//        // given
////        captor.firstValue.onSuccess(SmsModel(Random().nextInt(), UUID.randomUUID().toString()))
////        favoritesViewModel.getFavorites().observeForever(mockFavoriteViewObserver)
////        smsTestObserver = mockSmsController.observeIncomingSms().test()
//        // when
////        favoritesViewModel.getFavorites()
//        // then
//
//
//        /**
//
//        val list = BufferooFactory.makeBufferooList(2)
//        val viewList = BufferooFactory.makeBufferooViewList(2)
//        stubBufferooMapperMapToView(viewList[0], list[0])
//        stubBufferooMapperMapToView(viewList[1], list[1])
//
//        bufferoosViewModel.getBufferoos()
//
//        verify(getBufferoos).execute(captor.capture(), eq(null))
//        captor.firstValue.onNext(list)
//
//        assert(bufferoosViewModel.getBufferoos().value?.status == ResourceState.SUCCESS)
//
//
//
//         */
//        // given
//        favoritesViewModel.getFavorites().observeForever(observer)
//        val code = DataFactory.randomInt()
//        favoritesViewModel.onEtaRequested(TestModelsFactory.generateFavoriteView(code))
//        // when
//        smsResult.onNext(TestModelsFactory.generateSmsModel(code))
//        // then
////        prin("The data is ${favoritesViewModel.getFavorites().value?.data}")
//        assert(favoritesViewModel.getFavorites().value?.data?.any { it.code == code } ?: false)
//    }
//
//    @Ignore("Ignored test: when sms is received correct data is passed -> Lacking implementation")
//    @Test
//    @Throws(IllegalArgumentException::class)
//    fun `when sms is received single is completed`() {
//
//    }
//
//    @Ignore("Ignored test: implement test")
//    @Test
//    @Throws(IllegalArgumentException::class)
//    fun `when sms request errors the error state posted`() {
//
//    }

//    @Test
//    @Throws(Exception::class)
//    fun `favorites observable correctly emitted values`() {
//        val testSubscriber = TestSubscriber<Int>()
//        Observable.just(1).subscribe(testSubscriber)
//        testSubscriber.assertNoErrors()
//        testSubscriber.assertReceivedOnNext(Arrays.asList(1))
//    }
//}