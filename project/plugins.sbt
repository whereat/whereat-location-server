logLevel := Level.Warn
resolvers += Resolver.url("heroku-sbt-plugin-releases", url("http://dl.bintray.com/heroku/sbt-plugins/"))(Resolver.ivyStylePatterns)
resolvers += Resolver.url("bintray-sbt-plugin-releases", url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.1")
resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.3")
addSbtPlugin("com.heroku" % "sbt-heroku" % "0.3.0")
addSbtPlugin("au.com.onegeek" %% "sbt-dotenv" % "1.1.33")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.2")
