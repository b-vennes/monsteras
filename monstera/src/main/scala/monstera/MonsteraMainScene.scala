package monstera

import indigo.scenes.{Scene, SceneContext, SceneName}
import indigo.shared.Outcome
import indigo.shared.events.{EventFilters, GlobalEvent}
import indigo.shared.scenegraph.SceneUpdateFragment
import indigo.shared.subsystems.SubSystem
import indigo.shared.utils.Lens

object MonsteraMainScene extends Scene[Unit, Model, ViewModel]:

  type SceneModel = Model
  type SceneViewModel = ViewModel

  override def name: SceneName = SceneName("main")

  override def modelLens: Lens[Model, Model] = Lens.identity

  override def viewModelLens: Lens[ViewModel, ViewModel] = Lens.identity

  override def eventFilters: EventFilters = EventFilters.AllowAll

  override def subSystems: Set[SubSystem[Model]] = Set()

  override def updateModel(
      context: SceneContext[Unit],
      model: Model
  ): GlobalEvent => Outcome[Model] = _ => Outcome(model)

  override def updateViewModel(
      context: SceneContext[Unit],
      model: Model,
      viewModel: ViewModel
  ): GlobalEvent => Outcome[ViewModel] = _ => Outcome(viewModel)

  override def present(
      context: SceneContext[Unit],
      model: Model,
      viewModel: ViewModel
  ): Outcome[SceneUpdateFragment] =
    Outcome(SceneUpdateFragment.empty)
