package com.alexwerff.layer

import zio.console.{Console, putStr}
import zio.stream.ZStream
import zio.{Has, ZLayer}



object TestDataRepo {
	type TestDataRepoLayer = Has[TestDataRepo.Service]

	trait Service {
		def getTestData(id:String):ZStream[Any,RepoError,TestData]
	}

	val defaultRepo: ZLayer[Console, Nothing, TestDataRepoLayer] = ZLayer.fromService { console =>
		new Service {
			override def getTestData(id:String): ZStream[Any, RepoError, TestData] =
				ZStream
					.fromIterable(Seq(1, 2, 3))
					.tap { value =>
						console.putStrLn(s"TestData:$value")
					}
					.map(TestData(id,_))
		}
	}

	case class TestData(id: String,value: Int)

	sealed trait RepoError
	object RepoError {
		case object ReadError extends RepoError
	}
}
