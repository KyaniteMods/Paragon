<p align="center">
    <img src="images/paragonbanner.png" width="600" height="150" title="Paragon Banner">
<br>
    <a href="https://modrinth.com/mod/paragon">
        <img src="https://img.shields.io/badge/-modrinth-gray?style=for-the-badge&labelColor=green&labelWidth=15&logo=appveyor&logoColor=white">
    </a>
    <a href="https://discord.gg/GDNRd5yvxa">
        <img src="https://img.shields.io/discord/1000916496484151308?label=kyanite%20mods&logo=discord&logoColor=white&style=for-the-badge">
    </a>
</p>

### Lightweight and easy-to-use config library. Used in Deeper and Darker, and Golems of All Types. Created by Kyanite Mods with 💖

## Advantages of using Paragon:
- Lightweight and modular, Paragon itself is just a config library for other mods to use. It does not add any in-game content (such as menus, GUIs, etc)
- Super easy to use and setup
- Modern and simple

[Example usage](https://github.com/KyaniteMods/Paragon/tree/master/common/src/main/java/com/kyanite/paragon/example)

## Installation
### Repository (add to repositories)
````gradle
maven {
    name = "Modrinth"
    url = "https://api.modrinth.com/maven"
    content {
        includeGroup "maven.modrinth"
    }
}
````
### Fabric (remove include if you dont want paragon packed into your jar)
````gradle
include modImplementation("maven.modrinth:paragon:forge-1.0.0b-1.19.2")
````
### Forge
````gradle
implementation fg.deobf('maven.modrinth:paragon:forge-1.0.0b-1.19.2')
````
#### Common (for Architectury projects)
````gradle
modImplementation("maven.modrinth:paragon:common-1.0.0b-1.19.2")
````