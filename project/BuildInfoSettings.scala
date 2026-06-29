import com.github.sbt.git.GitPlugin.autoImport.git
import sbt.Keys._
import sbt.{Def, SettingKey}
import sbtbuildinfo.BuildInfoKeys.buildInfoKeys
import sbtbuildinfo.BuildInfoPlugin.autoImport.{buildInfoOptions, buildInfoPackage, BuildInfoKey, BuildInfoOption}
import sbt.KeyRanks

object BuildInfoSettings {

  private val gitCommitString =
    SettingKey[String]("gitCommit").withRank(KeyRanks.Invisible)

  val value: Seq[Def.Setting[?]] = Seq(
    buildInfoKeys    := Seq(BuildInfoKey(name), BuildInfoKey(version), BuildInfoKey(scalaVersion), BuildInfoKey(sbtVersion), BuildInfoKey(gitCommitString)),
    buildInfoPackage := s"${organization.value}.api",
    buildInfoOptions ++= Seq(BuildInfoOption.ToJson, BuildInfoOption.BuildTime),
    gitCommitString  := git.gitHeadCommit.value.getOrElse("unavailable")
  )

}
