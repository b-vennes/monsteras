package monstera

import indigo.scenes.{Scene, SceneName}
import indigo.shared.collections.NonEmptyList
import indigo.shared.events.FrameTick
import indigo.shared.{Context, Outcome, Startup}
import indigo.{
  AssetCollection,
  BootResult,
  Dice,
  EventFilters,
  GlobalEvent,
  IndigoGame,
  SceneUpdateFragment
}
import monstera.generated.{Assets, AurebeshRedFont, Config}

import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object MonsteraGame extends IndigoGame[Unit, Unit, Model, ViewModel]:

  override def scenes(
      bootData: Unit
  ): NonEmptyList[Scene[Unit, Model, ViewModel]] =
    NonEmptyList(MonsteraMainScene)

  override def initialScene(bootData: Unit): Option[SceneName] =
    Some(MonsteraMainScene.name)

  override def eventFilters: EventFilters = EventFilters.AllowAll

  override def boot(
      flags: Map[String, String]
  ): Outcome[BootResult[Unit, Model]] =
    Outcome(
      BootResult(Config.config.withMagnification(2), ())
        .addAssets(Assets.assets.assetSet ++ Assets.assets.generated.assetSet)
        .addFonts(AurebeshRedFont.fontInfo)
    )

  override def setup(
      bootData: Unit,
      assetCollection: AssetCollection,
      dice: Dice
  ): Outcome[Startup[Unit]] = Outcome(Startup.Success(()))

  val monsteraBirthday = Day(135, 2025)
  override def initialModel(startupData: Unit): Outcome[Model] =
    Outcome(
      Model(
        Day.today,
        Monstera(
          monsteraBirthday
        )
      )
    )

  override def initialViewModel(
      startupData: Unit,
      model: Model
  ): Outcome[ViewModel] =
    Outcome(
      ViewModel(
        Assets.assets.BgMaterial,
        Monstera.findBitmap(0),
        Coffee(
          (242, 111),
          (250, 113),
          (256, 111)
        )
      )
    )

  override def updateModel(
      context: Context[Unit],
      model: Model
  ): GlobalEvent => Outcome[Model] =
    case _ => Outcome(model)

  override def updateViewModel(
      context: Context[Unit],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] =
    case FrameTick =>
      viewModel.update(model, context.frame.time.delta, context.frame.dice)
    case _ => Outcome(viewModel)

  override def present(
      context: Context[Unit],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    viewModel.render()
