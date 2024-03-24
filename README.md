# ziohttp3hanging

https://github.com/zio/zio-http/issues/2742

```shell
sbt test
# The test should succeed with output like:
# [info] MyWsClientPactTest:
# [info] MyWsClient
# [info] - should retrieve a model
# [info] Run completed in 3 seconds, 99 milliseconds.
# [info] Total number of tests run: 1
# [info] Suites: completed 1, aborted 0
# [info] Tests: succeeded 1, failed 0, canceled 0, ignored 0, pending 0
# [info] All tests passed.

# Optionally the test may fail with a pact server issue
# Cause: io.netty.channel.AbstractChannel$AnnotatedConnectException: finishConnect(..) failed: Connexion refusée: localhost/[0:0:0:0:0:0:0:1]:33523
# Ignore this case, and just retry

# But for now, the test hangs forever:
# ....
#   | => root / Test / executeTests 20s

# Changing the headers value in MyWsClient workaround solve the hanging
```
