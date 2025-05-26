import scala.sys.process._
import scala.language.postfixOps

import sbtwelcome._
import indigoplugin._

Global / onChangedBuildSource := ReloadOnSourceChanges

Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }

lazy val gameOptions: IndigoOptions =
  IndigoOptions.defaults
    .withTitle("Monstera")
    .withWindowSize(800, 480)
    .withBackgroundColor("white")
    .withAssetDirectory("monstera/assets")
    .excludeAssets {
      case p if p.endsWith(os.RelPath.rel / ".gitkeep") => true
      case _                                            => false
    }

lazy val monstera =
  project
    .enablePlugins(ScalaJSPlugin, SbtIndigo)
    .settings( // Normal SBT settings
      name         := "Monstera",
      version      := "0.0.1",
      scalaVersion := "3.7.0",
      organization := "monstera",
      libraryDependencies ++= Seq(
        "io.github.cquiroz" %%% "scala-java-time" % "2.6.0",
        "org.scalameta" %%% "munit" % "1.1.1" % Test
      ),
      testFrameworks += new TestFramework("munit.Framework"),
      scalafixOnCompile  := true,
      semanticdbEnabled  := true,
      semanticdbVersion  := scalafixSemanticdb.revision,
    )
    .settings( // Indigo specific settings
      indigoOptions := gameOptions,
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "indigo-json-circe" % "0.21.1",
        "io.indigoengine" %%% "indigo"            % "0.21.1",
        "io.indigoengine" %%% "indigo-extras"     % "0.21.1"
      ),
      Compile / sourceGenerators += Def.task {
        IndigoGenerators("monstera.generated")
          .generateConfig("Config", gameOptions)
          .embedFont(
            moduleName = "AurebeshRedFont",
            font = os.Path((Compile / baseDirectory).value) / "generator-data" / "AurebeshRed-Regular.otf",
            fontOptions = FontOptions(
              fontKey = "AurebeshRed",
              fontSize = 10,
              charSet = CharSet.ASCII,
              color = RGB.Black,
              antiAlias = false,
              layout = FontLayout.normal
            ),
            imageOut = os.Path((Compile / baseDirectory).value) / "assets" / "generated"
          )
          .listAssets(
            "Assets",
            gameOptions.assets
              .withAssetDirectory(
                os.Path((Compile / baseDirectory).value / "assets")
                  .relativeTo(os.Path((ThisBuild / baseDirectory).value))
              )
              .withExclude(_.endsWith(os.RelPath(".DS_Store")))
          )
          .toSourceFiles((Compile / sourceManaged).value)
      }
    )

lazy val indigo =
  project
    .in(file("."))
    .settings(
      logo := "Monstera (v" + version.value.toString + ")",
      usefulTasks := Seq(
        UsefulTask("runGame", "Run the game").noAlias,
        UsefulTask("buildGame", "Build web version").noAlias,
        UsefulTask("runGameFull", "Run the fully optimised game").noAlias,
        UsefulTask("buildGameFull", "Build the fully optimised web version").noAlias
      ),
      logoColor        := scala.Console.MAGENTA,
      aliasColor       := scala.Console.YELLOW,
      commandColor     := scala.Console.CYAN,
      descriptionColor := scala.Console.WHITE
    )
    .aggregate(monstera)

addCommandAlias(
  "buildGame",
  List(
    "monstera/compile",
    "monstera/fastLinkJS",
    "monstera/indigoBuild"
  ).mkString(";", ";", "")
)
addCommandAlias(
  "buildGameFull",
  List(
    "monstera/compile",
    "monstera/fullLinkJS",
    "monstera/indigoBuildFull"
  ).mkString(";", ";", "")
)
addCommandAlias(
  "runGame",
  List(
    "monstera/compile",
    "monstera/fastLinkJS",
    "monstera/indigoRun"
  ).mkString(";", ";", "")
)
addCommandAlias(
  "runGameFull",
  List(
    "monstera/compile",
    "monstera/fullLinkJS",
    "monstera/indigoRunFull"
  ).mkString(";", ";", "")
)
