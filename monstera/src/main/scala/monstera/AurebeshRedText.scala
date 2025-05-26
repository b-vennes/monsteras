package monstera

import indigo.shared.scenegraph.Text
import monstera.generated.Assets.assets.generated.AurebeshRedFontMaterial
import monstera.generated.AurebeshRedFont

object AurebeshRedText:
  def apply(text: String, x: Int, y: Int) =
    Text(text, x, y, AurebeshRedFont.fontKey, AurebeshRedFontMaterial)
