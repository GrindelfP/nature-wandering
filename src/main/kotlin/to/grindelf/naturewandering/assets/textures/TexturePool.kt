package to.grindelf.naturewandering.assets.textures

import to.grindelf.naturewandering.IsometricWorldConstants.GRASS_TEXTURES_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.PORTAL_TEXTURES_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.STONES_TEXTURES_PATH
import to.grindelf.naturewandering.IsometricWorldConstants.TREES_TEXTURES_PATH

data class TexturePool(
    val grassTextureSet: GrassTextureSet = GrassTextureSet(),
    val stonesTextureSet: StonesTextureSet = StonesTextureSet(),
    val treesTextureSet: TreesTextureSet = TreesTextureSet(),
    val portalTextureSet: PortalTextureSet = PortalTextureSet()
)

abstract class TextureSet

class GrassTextureSet : TextureSet() {

    val groundTextures = TextureAssetLoader.loadTexturesAt(GRASS_TEXTURES_PATH)

}

class StonesTextureSet : TextureSet() {

    val stonesTextures = TextureAssetLoader.loadTexturesAt(STONES_TEXTURES_PATH)

}

class TreesTextureSet : TextureSet() {

    val treesTextures = TextureAssetLoader.loadTexturesAt(TREES_TEXTURES_PATH)

}

class PortalTextureSet : TextureSet() {

    val portalTextures = TextureAssetLoader.loadTexturesAt(PORTAL_TEXTURES_PATH)

}
