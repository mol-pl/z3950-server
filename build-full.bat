@echo off

rem Prepare Java and Maven
CALL __setup_env.bat

call %M2_HOME%\bin\mvn.cmd -Dfile.encoding=UTF-8 -Pfull clean
timeout 5
rem call %M2_HOME%\bin\mvn.cmd -Dfile.encoding=UTF-8 -Pfull install
rem Continue on test failures (runs tests to the end)
rem --fail-at-end  Only fail the build after running tests (e.g. finish building core and then fail projects that depend on core)
rem --fail-never   Never fail the build, regardless of test result (will still run tests and report failuers, but will finish building whole(!) project if possible)
call %M2_HOME%\bin\mvn.cmd -Dfile.encoding=UTF-8 --fail-never install

PAUSE
