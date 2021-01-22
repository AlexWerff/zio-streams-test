package com.alexwerff

import com.alexwerff.layer.{RandomDataRepo, RepoReader, TestDataRepo}
import RepoReader.RepoReaderLayer
import io.moia.ziotest.layer.RepoReader
import zio.console._
import zio._

object App extends zio.App {

	val repoReaderLayer: ZLayer[Console, Nothing, RepoReaderLayer] =
		(TestDataRepo.defaultRepo ++ RandomDataRepo.defaultRepo) >>> RepoReader.defaultService

	def run(args: List[String]): URIO[ZEnv with Console, ExitCode] =
		myAppLogic
			.provideLayer(repoReaderLayer)
			.exitCode


	val myAppLogic: ZIO[RepoReaderLayer, Unit, Unit] =
		ZIO.accessM(_.get.readData(Seq("1")).runDrain.ignore)
}
