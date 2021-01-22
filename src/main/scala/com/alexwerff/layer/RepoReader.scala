package com.alexwerff.layer

import RandomDataRepo.RandomDataRepoLayer
import TestDataRepo.TestDataRepoLayer
import zio._
import zio.stream._

object RepoReader {

	type TestDataWithRandomData = Any with TestDataRepoLayer with RandomDataRepoLayer
	type RepoReaderLayer = Has[RepoReader.Service]

	trait Service {
		def readData(ids: Seq[String]): ZStream[Any, ReaderError,ReposData]
	}



	val defaultService: ZLayer[TestDataWithRandomData, Nothing, RepoReaderLayer] =
		ZLayer.fromServices[TestDataRepo.Service, RandomDataRepo.Service, RepoReader.Service] { (testData, randomData) =>
			new RepoReader.Service {
				override def readData(ids: Seq[String]): ZStream[Any, ReaderError, ReposData] = {
					Stream
						.fromIterable(ids)
						.flatMap{ id =>
							val readDataStream = randomData.getRandomData(id) <*> testData.getTestData(id)
							readDataStream
								.mapError(_ => ReaderError.ReadError)
								.map{ case (random, test) =>
									ReposData(id,random, test)
								}
						}
				}
			}
		}

	case class ReposData(id:String, randomData: RandomDataRepo.RandomData, testData: TestDataRepo.TestData)

	sealed trait ReaderError
	object ReaderError {
		case object ReadError extends ReaderError
	}
}
