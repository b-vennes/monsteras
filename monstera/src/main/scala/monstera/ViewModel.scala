package monstera

import indigo.shared.Outcome
import indigo.shared.dice.Dice
import indigo.shared.materials.Material.Bitmap
import indigo.shared.scenegraph.{Graphic, SceneUpdateFragment}
import indigo.shared.time.Seconds

final case class ViewModel(
    background: Background,
    monsteraBitmap: Bitmap,
    coffee: Coffee
):
  def update(model: Model, delta: Seconds, dice: Dice): Outcome[ViewModel] =
    Outcome(
      copy(
        background = background.update(delta.toMillis),
        monsteraBitmap = Monstera.findBitmap(model.monsteraAge),
        coffee = coffee.update(delta, dice)
      )
    )

  def render: Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        Graphic(
          400,
          240,
          background.render
        ).moveTo(0, 0),
        Graphic(
          400,
          240,
          monsteraBitmap
        ).moveTo(0, -10)
      )
    ).flatMap(view =>
      coffee
        .render()
        .map(view |+| _)
    )
