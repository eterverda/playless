Maven repository for playless argifacts.

Projects itself is on master branch.

To add playless to your project first add this maven repository:
```
repositories {
  maven {
    url 'https://raw.githubusercontent.com/eterverda/playless/m2/'
  }
}
```

Then add playless dependency:
```
dependencies {
  compile('io.githib.eterverda.playless:playless-lib:0.2.0@aar') {
    transitive = true
  }
}
```
