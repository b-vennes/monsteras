package monstera

import indigo.logger
import indigo.shared.Outcome
import indigo.shared.datatypes.Vector2
import indigo.shared.dice.Dice
import indigo.shared.scenegraph.{Graphic, SceneUpdateFragment}
import indigo.shared.time.Seconds
import monstera.Coffee.SteamParticle.rollSteamParticle
import monstera.generated.Assets

final case class Coffee(
    steam: List[Coffee.Steam]
):
  def update(delta: Seconds, dice: Dice): Coffee =
    copy(steam = steam.map(_.update(delta, dice)))

  def render(): Outcome[SceneUpdateFragment] =
    Outcome(
      SceneUpdateFragment(
        Graphic(
          64,
          64,
          Assets.assets.CoffeeMaterial
        ).moveTo(250, 110),
        Graphic(
          32,
          32,
          Assets.assets.SteambgMaterial
        ).moveTo(255, 75),
        Graphic(
          32,
          32,
          Assets.assets.SteambgMaterial
        ).moveTo(260, 100),
      )
    ).flatMap: current =>
      steam
        .map(_.render())
        .foldLeft(Outcome(current)):
          case (current, renderedSteam) =>
            renderedSteam.flatMap(r => current.map(_ |+| r))

object Coffee:
  val updateEvery = Seconds(1 / 60.0)

  def apply(steam: (Double, Double)*): Coffee =
    Coffee(
      steam.toList
        .map(point => Coffee.Steam(Vector2(point._1, point._2)))
    )

  extension (d: Dice)
    def rollDoubleRange(min: Double, max: Double): Double =
      val diff = Math.abs(max - min)
      val rangeMin = Math.min(min, max)

      (d.rollDouble * diff) + rangeMin

  final case class SteamParticle(
      location: Vector2,
      velocity: Vector2,
      acceleration: Vector2,
      age: Short,
      life: Short
  ):
    import SteamParticle.accChanges

    def update(dice: Dice): SteamParticle =
      copy(
        age = (1 + age).toShort,
        location = location + velocity,
        velocity = velocity + acceleration,
        acceleration = Vector2(
          acceleration.x + dice.rollDoubleRange(
            accChanges.x.min,
            accChanges.x.max
          ),
          acceleration.y + dice.rollDoubleRange(
            accChanges.y.min,
            accChanges.y.max
          )
        )
      )

  object SteamParticle:
    val startOffset = Vector2(23, 7)
    val startRange = (-1, 1)
    val lifeRange = (30, 120)

    val startVelocityRanges
        : (x: (min: Double, max: Double), y: (min: Double, max: Double)) =
      (
        (-0.1, 0.1),
        (-0.5, -0.1)
      )
    val startAccRanges
        : (x: (min: Double, max: Double), y: (min: Double, max: Double)) =
      (
        (-0.0001, 0.0001),
        (-0.00001, 0.00005)
      )
    val accChanges
        : (x: (min: Double, max: Double), y: (min: Double, max: Double)) =
      (
        (-0.0001, 0.0001),
        (-0.00001, 0.00005)
      )

    extension (d: Dice)
      def rollSteamParticle(location: Vector2): SteamParticle =
        val startLocation =
          location +
            startOffset +
            Vector2(
              d.rollRange(startRange._1, startRange._2),
              d.rollRange(startRange._1, startRange._2)
            )
        val velocity = Vector2(
          d.rollDoubleRange(
            startVelocityRanges.x.min,
            startVelocityRanges.x.max
          ),
          d.rollDoubleRange(
            startVelocityRanges.y.min,
            startVelocityRanges.y.max
          )
        )
        logger.debug(velocity.toString)
        val acceleration = Vector2(
          d.rollDoubleRange(
            startAccRanges.x.min,
            startAccRanges.x.max
          ),
          d.rollDoubleRange(
            startAccRanges.y.min,
            startAccRanges.y.max
          )
        )
        val life = d.rollRange(lifeRange._1, lifeRange._2).toShort

        SteamParticle(
          startLocation,
          velocity,
          acceleration,
          0,
          life
        )

  opaque type Particles = List[SteamParticle]

  object Particles:
    val empty: Particles = Nil

  extension (p: Particles)
    def update(dice: Dice): Particles =
      p.map(_.update(dice)).filter(p => p.age < p.life)

    def update(dice: Dice)(add: SteamParticle): Particles =
      (add :: p).update(dice)

    def count(): Int = p.length

    def render(): Outcome[SceneUpdateFragment] =
      Outcome(
        SceneUpdateFragment(
          (
            p.map: particle =>
              val (size, material) = particle.age match
              case x if x < 60 => (4, Assets.assets.ParticleSMaterial)
              case x if x < 100 => (8, Assets.assets.ParticleMMaterial)
              case _ => (16, Assets.assets.ParticleLMaterial)
              Graphic(
                size,
                size,
                material
              ).moveTo(particle.location.toPoint)
          )*
        )
      )

  final case class Steam(
      location: Vector2,
      particles: Particles,
      updateDelta: Seconds
  ):
    def update(delta: Seconds, dice: Dice): Steam =
      if updateDelta > updateEvery
      then
        val newParticles = for
          _ <- 1 to dice.rollRange(
            0,
            Math.max(0, Steam.maxParticles - particles.length)
          )
          particle = dice.rollSteamParticle(location)
        yield particle
        copy(
          particles =
            particles.update(dice).filter(p => p.age < p.life) ++ newParticles
        )
      else copy(updateDelta = updateDelta + delta)

    def render(): Outcome[SceneUpdateFragment] =
      particles.render()

  object Steam:
    val maxParticles = 3

    def apply(location: Vector2): Steam =
      Steam(
        location,
        Particles.empty,
        Seconds(0)
      )
