package com.joaquimley.transporteta.sharedpreferences.mapper

import org.junit.Before


// TODO Nothing has been tested or implemented ¯\_(ツ)_/¯¯
class SharedPrefTransportMapperTest {

    private val robot = Robot()
    private lateinit var mapper: SharedPrefTransportMapper

    @Before
    fun setup() {
        mapper = SharedPrefTransportMapper()
    }

//    @Test
//    fun fromModelToView() {
//        // Assemble
//        val stubbed = TransportFactory.makeTransport()
//        // Act
//        val mapped = mapper.toEntity(stubbed)
//        // Assert
//        assert(robot.areItemsTheSame(stubbed, mapped))
//    }
//
//    @Test
//    fun fromModelListToViewList() {
//        // Assemble
//        val stubbed = TransportFactory.makeTransportList(5)
//        // Act
//        val mapped = mapper.toEntity(stubbed)
//        // Assert
//        assert(robot.areItemsInListTheSame(stubbed, mapped))
//    }
//
//    @Test
//    fun fromViewToModel() {
//        // Assemble
//        val stubbed = TransportFactory.makeTransportEntity()
//        // Act
//        val mapped = mapper.toModel(stubbed)
//        // Assert
//        assert(robot.areItemsTheSame(mapped, stubbed))
//    }
//
//    @Test
//    fun fromViewListToModelList() {
//        // Assemble
//        val stubbed = TransportFactory.makeTransportEntityList(5)
//        // Act
//        val mapped = mapper.toModel(stubbed)
//        // Assert
//        assert(robot.areItemsInListTheSame(mapped, stubbed))
//    }

    inner class Robot {
//        fun areItemsInListTheSame(transportList: List<Transport>, transportViewList: List<TransportEntity>): Boolean {
//            for (transport in transportList.withIndex()) {
//                if (!areItemsTheSame(transportList[transport.index], transportViewList[transport.index])) {
//                    return false
//                }
//            }
//            return true
//        }
//
//        fun areItemsTheSame(transport: Transport, transportView: TransportEntity): Boolean {
//            return transport.id == transportView.id &&
//                    transport.code == transportView.code &&
//                    transport.latestEta == transportView.latestEta &&
//                    transport.isFavorite == transportView.isFavorite &&
//                    transport.type.equals(transportView.type)
//        }
    }
}
