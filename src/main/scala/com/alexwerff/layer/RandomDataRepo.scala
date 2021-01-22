package com.alexwerff.layer

import zio.console.Console
import zio.stream.ZStream
import zio.{Has, ZLayer}


object RandomDataRepo {
	type RandomDataRepoLayer = Has[RandomDataRepo.Service]

	trait Service {
		def getRandomData(id:String):ZStream[Any,RepoError,RandomData]
	}

	val defaultRepo: ZLayer[Console, Nothing, RandomDataRepoLayer] = ZLayer.fromService { console =>
		new Service {
			override def getRandomData(id: String): ZStream[Any, RepoError, RandomData] =
				ZStream
					.fromIterable(Seq(1, 2, 3, 4, 5, 6))
					.tap { value =>
						console.putStrLn(s"RandomData:$value")
					}
					.map(RandomData(id,_))
		}
	}

	case class RandomData(id:String, value: Int)

	sealed trait RepoError
	object RepoError {
		case object ReadError extends RepoError
	}
}
