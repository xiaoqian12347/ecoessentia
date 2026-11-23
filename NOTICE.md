# Copyright and third-party notices

ECO Essentia Storage — Copyright (c) 2026 xiaoqian.

This project is distributed under GNU GPL version 3 only (`GPL-3.0-only`),
including its Java source and ECO-derived resource modifications. See LICENSE.
The former template LGPL license has been replaced to align the distribution
with its GPL ECO-derived resources.

## NovaEngineering ECO AE Extension

- Upstream: https://github.com/sddsd2332/NovaEngineering-ECOAEExtension
- Reference release: 1.2.0
- License: GNU GPL version 3; upstream license:
  https://github.com/sddsd2332/NovaEngineering-ECOAEExtension/blob/main/LICENSE
- Source release:
  https://github.com/sddsd2332/NovaEngineering-ECOAEExtension/releases/tag/1.2.0

The storage-cell sprites, their CTM bloom metadata, drive model geometry and
multipart state layout, and controller GUI atlas originate from ECO 1.2.0.
They have been adapted by xiaoqian for the essentia channel, namespaced under
`ecoessentia`, with modified purple indicators and item status textures.
Original authors retain copyright in their contributions. ECO's shared casing
and status-light resources remain external runtime references.

## External dependencies

Cleanroom, AE2 Extended Life, Thaumic Energistics Extended Life, Thaumcraft,
Baubles, MMCE, The One Probe, and MixinBooter are external dependencies and
retain their respective licenses. Their binaries are not embedded in this
mod's release or source archive. ECO's binary is downloaded separately only
for building; its exact download and SHA-256 are pinned in
`scripts/prepare_dependencies.py`.

The Gradle wrapper is retained from the Cleanroom development template.
Gradle is licensed under Apache License 2.0:
https://github.com/gradle/gradle/blob/master/LICENSE
