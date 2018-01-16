pipelineJob("test") {
	description("This builds app from pull requests")
	keepDependencies(false)
	blockOn("""Build-iOS-App
				Build-Android-App
				Run-iOS-Tests
				Run-Android-Tests""", {
		blockLevel("GLOBAL")
		scanQueueFor("DISABLED")
	})
	parameters {
		stringParam("GIT_BRANCH", "dev", "Name of the branch that will be checked out from Repo")
		stringParam("SELECTED_BINARY", "none", "Local path of binary to run the tests against")
		booleanParam("REAL_DEVICE", false, """Check this if you want to run on a plugged in device instead of the simulator
						Make sure only one device is plugged.
						UDID will be automatically fetched""")
		choiceParam("PLATFORM", ["Android", "iOS"], "Select the platform to test")
	}
	environmentVariables {
		env("PLATFORM", "iOS")
		env("VARIABLE", "value")
		loadFilesFromMaster(false)
		groovy()
		keepSystemVariables(true)
		keepBuildVariables(true)
		overrideBuildParameters(false)
	}
	disabled(true)
	quietPeriod(5)
	displayName("Pipeline")
	concurrentBuild(false)
	steps {
		dsl {
			text("def String gitUrl = 'alandoni/xml-job-to-dsl'")
			ignoreExisting(false)
			removeAction("DELETE")
			removeViewAction("IGNORE")
			lookupStrategy("JENKINS_ROOT")
		}
		buildNameUpdater {
			buildName("")
			macroTemplate("Build App")
			fromFile(false)
			fromMacro(true)
			macroFirst(true)
		}
		shell("""export PLATFORM=iOS
				cd 'xml-job-to-dsl/src/scripts/'
				./run.sh""")
		downstreamParameterized {
			trigger("Pipeline") {
				block {
					buildStepFailure("FAILURE")
					failure("FAILURE")
					unstable("UNSTABLE")
				}
				parameters {
					booleanParam("DEPLOY_TO_CRASHLYTICS", false)
					booleanParam("REAL_DEVICE", false)
					predefinedProps([EMAILS: "alan_doni@hotmail.com",
								REMOTE_USER: "jenkins",
								LOG_LEVEL: "warn",
								GIT_BRANCH: "dev",
								SELECTED_BINARY: "none"])
				}
			}
		}
		gradle {
			switches()
			tasks("clean :apps:blackberry:assembleRelease :apps:blackberry:crashlyticsUploadDistributionRelease")
			fromRootBuildScriptDir()
			buildFile()
			gradleName("(Default)")
			useWrapper(true)
			makeExecutable(true)
			useWorkspaceAsHome(false)
		}
	}
	publishers {
		archiveArtifacts {
			pattern("build/**/*")
			allowEmpty(false)
			defaultExcludes(true)
			fingerprint(false)
			onlyIfSuccessful(false)
		}
		richTextPublisher {
			stableText("""${FILE:xml-job-to-dsl/tests_report.html}
				${FILE:build_variables.html}""")
			unstableText("")
			failedText("")
			abortedText("")
			nullAction("")
			unstableAsStable(true)
			failedAsStable(true)
			abortedAsStable(true)
			parserName("HTML")
		}
		extendedEmail {
			recipientList("alan_doni@hotmail.com")
			triggers {
				always {
					subject("$PROJECT_DEFAULT_SUBJECT")
					content("$PROJECT_DEFAULT_CONTENT")
					attachmentPatterns()
					attachBuildLog(false)
					compressBuildLog(false)
					replyToList("$PROJECT_DEFAULT_REPLYTO")
					contentType("project")
				}
			}
			contentType("default")
			defaultSubject("$DEFAULT_SUBJECT")
			defaultContent("$DEFAULT_CONTENT")
			attachmentPatterns()
			preSendScript("$DEFAULT_PRESEND_SCRIPT")
			attachBuildLog(true)
			compressBuildLog(false)
			replyToList("$DEFAULT_REPLYTO")
			saveToWorkspace(false)
			disabled(false)
		}
		postBuildScripts {
			steps {
				steps {
					shell("""git tag "beta-$BUILD_NUMBER"
									git push origin --tags
									git remote prune origin""")
				}
			}
			markBuildUnstable(false)
			onlyIfBuildSucceeds(true)
			onlyIfBuildFails(false)
			markBuildUnstable(false)
		}
		mailer("alan_doni@hotmail.com.com", false, false)
		downstream("Other-Project-Name", "SUCCESS")
		archiveJunit("*.xml") {
			healthScaleFactor(1.0)
			allowEmptyResults(false)
		}
		githubCommitNotifier()
	}
	wrappers {
		credentialsBinding {
			string("PASSWORD", '${PASS_WORD}')
			usernamePassword("GITHUB_CREDENTIALS", "jenkins")
		}
		timeout {
			absolute(30)
		}
		timestamps()
		preBuildCleanup {
			deleteDirectories(false)
			cleanupParameter()
		}
		sshAgent("0bdbb6ac-187e-473b-a2c2-12e4c6e87568")
	}
	logRotator(50)
	scm {
		git {
			remote {
				name("origin")
				github("alandoni/xml-job-to-dsl", "https")
				credentials("jenkins")
			}
			branch('*/${GIT_BRANCH}')
			extensions {
				wipeOutWorkspace()
			}
		}
	}
	definition {
		cpsScm {
			scm {
				git {
					remote {
						github("alandoni/xml-job-to-dsl", "https")
						credentials("jenkins")
					}
					branch('*/${GIT_BRANCH}')
					extensions {
						wipeOutWorkspace()
					}
				}
			}
			scriptPath("xml-job-to-dsl/src/scripts/pipeline.groovy")
		}
	}
	triggers {
		githubPullRequest {
			cron("H/5 * * * *")
			triggerPhrase("\\QJenkins, build this please\\E")
			permitAll(true)
		}
		githubPush()
		scm("H/2 * * * *") {
			ignorePostCommitHooks(false)
		}
	}
	configure {
		it / 'properties' / 'jenkins.model.BuildDiscarderProperty' {
			strategy {
				'daysToKeep'('50')
				'numToKeep'('-1')
				'artifactDaysToKeep'('-1')
				'artifactNumToKeep'('-1')
			}
		}
		it / 'properties' / 'com.coravy.hudson.plugins.github.GithubProjectProperty' {
			'projectUrl'('https://github.com/alandoni/xml-job-to-dsl/')
		}
		it / 'properties' / 'com.sonyericsson.rebuild.RebuildSettings' {
			'autoRebuild'('false')
			'rebuildDisabled'('false')
		}
		it / 'builders' / 'au.com.rayh.XCodeBuilder' {
			cleanBeforeBuild(true)
			cleanTestReports(false)
			configuration("Release")
			target()
			sdk()
			symRoot()
			buildDir()
			xcodeProjectPath("/Users/jenkins/jobs/job-name/workspace/Framework/FrameworkDemo")
			xcodeProjectFile()
			xcodebuildArguments()
			xcodeSchema("ios")
			xcodeWorkspaceFile("ios")
			cfBundleVersionValue('${BUILD_NUMBER}')
			cfBundleShortVersionStringValue()
			buildIpa(false)
			ipaExportMethod("ad-hoc")
			generateArchive(true)
			unlockKeychain(true)
			keychainName("none (specify one below)")
			keychainPath("/Users/Shared/Jenkins/Library/Keychains/login.keychain")
			keychainPwd("App-Name")
			developmentTeamName("none (specify one below)")
			developmentTeamID("ASD1321ASD")
			allowFailingBuildResults(false)
			ipaName('${MARKETING_VERSION}-${VERSION}')
			ipaOutputDirectory()
			provideApplicationVersion(true)
			changeBundleID(false)
			bundleID()
			bundleIDInfoPlistPath()
			interpretTargetAsRegEx(false)
			ipaManifestPlistUrl()
		}
	}
}
