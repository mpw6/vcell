#!/usr/bin/env bash

shopt -s -o nounset

if [ "$#" -ne 1 ]; then
    echo "usage:  deploy.sh includefile"
    exit -1
fi

includefile=$1
. $includefile

. $vcell_secretsDir/deploySecrets.include

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
echo "deploy directory is ${DIR}"

projectRootDir=`dirname $DIR`
echo "project root directory is ${projectRootDir}"
projectTargetDir=$projectRootDir/target

serverTargetDir=$projectRootDir/vcell-server/target

apiTargetDir=$projectRootDir/vcell-api/target
apiDocrootDir=$projectRootDir/vcell-api/docroot
apiWebappDir=$projectRootDir/vcell-api/webapp

adminTargetDir=$projectRootDir/vcell-admin/target
adminJarsDir=$adminTargetDir/maven-jars

clientTargetDir=$projectRootDir/vcell-client/target
clientJarsDir=$clientTargetDir/maven-jars

skip_install4j=false
skip_build=false
skip_download_solvers=false

if [ "$skip_download_solvers" = false ]; then
	installSolversScript="${projectRootDir}/localsolvers/installSolvers.sh"
	$installSolversScript
fi

#--------------------------------------------------------------------------
# build project, generate user help files, gather jar files
# 1) maven build (maven clean verify)
# 2) gather jar files needed by DocumentCompiler (maven dependency plugin)
# 3) generate user help files (run DocumentCompiler)
# 4) maven build (mvn clean verify ... puts help files into jar resources)
#---------------------------------------------------------------------------
if [ "$skip_build" = false ]; then
	cd $projectRootDir
	echo "removing old docs"
	rm -r $projectRootDir/vcell-client/src/main/resources/vcellDocs
	echo "build vcell"
	mvn clean install dependency:copy-dependencies
	if [ $? -ne 0 ]; then
		echo "failed first maven build: mvn clean install dependency:copy-dependencies"
		exit -1
	fi
	echo "run document compiler"
	java -cp $clientTargetDir/maven-jars/*:$clientTargetDir/* org.vcell.documentation.DocumentCompiler
	if [ $? -ne 0 ]; then
		echo "failed to build user help: DocumentCompiler"
		exit -1
	fi
	echo "force rebuild to pick up new resources - the help files"
	mvn clean install dependency:copy-dependencies
	if [ $? -ne 0 ]; then
		echo "failed second maven build: mvn clean install"
		exit -1
	fi
fi

deployRootDir=$DIR

deployInstall4jDir=$deployRootDir/client/install4j

isMac=false
if [[ "$OSTYPE" == "darwin"* ]]; then
	isMac=true
fi


if [[ "$vcell_server_os" = "mac64" ]]; then
	localsolversDir=localsolvers/mac64
	nativelibsDir=nativelibs/mac64
elif [[ "$vcell_server_os" = "linux64" ]]; then
	localsolversDir=localsolvers/linux64
	nativelibsDir=nativelibs/linux64
else
	echo "vcell server os specied as $vcell_server_os expecting either 'linux64' or 'macos'"
	exit -1
fi

#
# 
#
stagingRootDir=$projectTargetDir/server-staging/
stagingConfigsDir=$stagingRootDir/configs
stagingJarsDir=$stagingRootDir/jars
stagingVisToolDir=$stagingRootDir/visTool
stagingNativelibsDir=$stagingRootDir/$nativelibsDir
stagingInstallersDir=$projectRootDir/target/installers

projectSolversDir=$projectRootDir/$localsolversDir

installed_server_sitedir=$vcell_server_sitedir
installedConfigsDir=$installed_server_sitedir/configs
installedJarsDir=$installed_server_sitedir/jars
installedNativelibsDir=$installed_server_sitedir/$nativelibsDir
installedVisToolDir=$installed_server_sitedir/visTool
installedSolversDir=$installed_server_sitedir/$localsolversDir
installedTmpDir=$installed_server_sitedir/tmp
installedLogDir=$installed_server_sitedir/log
installedJmsBlobFilesDir=$installed_server_sitedir/blobFiles
installedHtclogsDir=$installed_server_sitedir/htclogs
installedJavaprefsDir=$installed_server_sitedir/javaprefs
installedSystemPrefsDir=$installed_server_sitedir/javaprefs/.systemPrefs
installedInstallersDir=$installed_server_sitedir/installers
installedPrimarydataDir=$vcell_primary_datadir
installedSecondarydataDir=$vcell_secondary_datadir
installedParalleldataDir=$vcell_parallel_datadir
installedExportDir=$vcell_export_dir
installedExportUrl=$vcell_export_url
installedMpichHomedir=$vcell_mpich_homedir
installedDocrootDir=$installed_server_sitedir/docroot
installedWebappDir=$installed_server_sitedir/webapp

pathto_server_sitedir=$vcell_pathto_sitedir
pathto_ConfigsDir=$pathto_server_sitedir/configs
pathto_JarsDir=$pathto_server_sitedir/jars
pathto_NativelibsDir=$pathto_server_sitedir/$nativelibsDir
pathto_VisToolDir=$pathto_server_sitedir/visTool
pathto_SolversDir=$pathto_server_sitedir/$localsolversDir
pathto_TmpDir=$pathto_server_sitedir/tmp
pathto_LogDir=$pathto_server_sitedir/log
pathto_JmsBlobFilesDir=$pathto_server_sitedir/blobFiles
pathto_HtclogsDir=$pathto_server_sitedir/htclogs
pathto_JavaprefsDir=$pathto_server_sitedir/javaprefs
pathto_SystemPrefsDir=$pathto_server_sitedir/javaprefs/.systemPrefs
pathto_InstallersDir=$pathto_server_sitedir/installers
pathto_DocrootDir=$pathto_server_sitedir/docroot
pathto_WebappDir=$pathto_server_sitedir/webapp
#pathto_PrimarydataDir=$vcell_primary_datadir
#pathto_SecondarydataDir=$vcell_secondary_datadir
#pathto_ParalleldataDir=$vcell_parallel_datadir
#pathto_ExportDir=$vcell_export_dir
#pathto_ExportUrl=$vcell_export_url
#pathto_MpichHomedir=$vcell_mpich_homedir


installedVisitExe=/share/apps/vcell2/visit/visit2.9/visit2_9_0.linux-x86_64/bin/visit
installedPython=/share/apps/vcell2/vtk/usr/bin/vcellvtkpython

#--------------------------------------------------------------
# gather jar files using maven dependency plugin
#--------------------------------------------------------------
cd $projectRootDir
echo "populate maven-jars"
mvn dependency:copy-dependencies
if [ $? -ne 0 ]; then
	echo "failed: mvn dependency:copy-dependencies"
	exit -1
fi
	
#---------------------------------------------------------------
# build install4j platform specific installers for VCell client
# 
# cd to install4j directory which contains the Vagrant box 
# definition and scripts for building install4J installers 
# for VCell client.
#
# installers are installed in project/target/installers directory
#---------------------------------------------------------------
install4jWorkingDir=$projectRootDir/target/install4j-working
install4jDeploySettings=$install4jWorkingDir/DeploySettings.include

I4J_pathto_Install4jWorkingDir=$vcell_I4J_pathto_mavenRootDir/target/install4j-working
I4J_pathto_Install4jDeploySettings=$I4J_pathto_Install4jWorkingDir/DeploySettings.include
I4J_pathto_vcellClientJar="$vcell_I4J_pathto_mavenRootDir/vcell-client/target/$vcell_vcellClientJarFileName"

mkdir -p $install4jWorkingDir
if [ -e $install4jDeploySettings ]; then
	rm $install4jDeploySettings
fi
touch $install4jDeploySettings

echo "i4j_pathto_install4jc=$vcell_I4J_pathto_install4jc"			>> $install4jDeploySettings
echo "compiler_updateSiteBaseUrl=$vcell_I4J_updateSiteBaseUrl"		>> $install4jDeploySettings
echo "compiler_vcellIcnsFile=$vcell_I4J_pathto_vcellIcnsFile"		>> $install4jDeploySettings
echo "compiler_mavenRootDir=$vcell_I4J_pathto_mavenRootDir"			>> $install4jDeploySettings
echo "compiler_softwareVersionString=$vcell_softwareVersionString"	>> $install4jDeploySettings
echo "compiler_Site=$vcell_site_camel"								>> $install4jDeploySettings
echo "compiler_vcellVersion=$vcell_version"							>> $install4jDeploySettings
echo "compiler_vcellBuild=$vcell_build"								>> $install4jDeploySettings
echo "compiler_rmiHosts=$vcell_rmihosts"							>> $install4jDeploySettings
echo "compiler_bioformatsJarFile=$vcell_bioformatsJarFile"			>> $install4jDeploySettings
echo "compiler_bioformatsJarDownloadURL=$vcell_bioformatsJarDownloadURL" >> $install4jDeploySettings
echo "compiler_vcellClientJarFilePath=$I4J_pathto_vcellClientJar"	>> $install4jDeploySettings
echo "compiler_applicationId=$vcell_applicationId"					>> $install4jDeploySettings
echo "i4j_pathto_jreDir=$vcell_I4J_pathto_jreDir"					>> $install4jDeploySettings
echo "i4j_pathto_secretsDir=$vcell_I4J_pathto_secretsDir"			>> $install4jDeploySettings
echo "install_jres_into_user_home=$vcell_I4J_install_jres_into_user_home"	>> $install4jDeploySettings
echo "i4j_pathto_install4JFile=$vcell_I4J_pathto_installerFile"		>> $install4jDeploySettings
echo "i4j_pathto_mavenRootDir=$vcell_I4J_pathto_mavenRootDir"		>> $install4jDeploySettings

if [ "$skip_install4j" = false ]; then
	cd $deployInstall4jDir
	if [ "$vcell_I4J_use_vagrant" = true ]; then
	
		if [ "$vcell_I4J_install_jres_into_user_home" = true ] && [ ! -d $vcell_I4J_jreDir ]; then
			echo "expecting to find directory $vcell_I4J_jreDir with downloaded JREs compatible with Install4J configuration"
			exit -1
		fi
		
		echo "starting Vagrant box to run Install4J to target all platforms"
		vagrant up
		
		echo "invoking script on vagrant box to build installers"
		vagrant ssh -c "/vagrant/build_installers.sh $I4J_pathto_Install4jDeploySettings"
		i4j_retcode=$?
		
		echo "shutting down vagrant"
		vagrant halt
	else
		$DIR/client/install4j/build_installers.sh $install4jDeploySettings
		i4j_retcode=$?
	fi
	
	if [ $i4j_retcode -eq 0 ]; then
		echo "client-installers built"
	else
		echo "client-installer build failed"
		exit -1;
	fi
	
	echo "client install4j installers located in $stagingInstallersDir"
fi

cd $DIR

#-------------------------------------------------------
# build server-staging area
#-------------------------------------------------------
#
# build stagingDir/configs
#
mkdir -p $stagingRootDir
mkdir -p $stagingConfigsDir
mkdir -p $stagingJarsDir
mkdir -p $stagingVisToolDir
mkdir -p $stagingNativelibsDir

cp -p $adminTargetDir/maven-jars/*.jar $stagingJarsDir
cp -p $adminTargetDir/$vcell_vcellAdminJarFileName $stagingJarsDir
cp -p $apiTargetDir/maven-jars/*.jar $stagingJarsDir
cp -p $apiTargetDir/$vcell_vcellApiJarFileName $stagingJarsDir
cp -p $serverTargetDir/maven-jars/*.jar $stagingJarsDir
cp -p $serverTargetDir/$vcell_vcellServerJarFileName $stagingJarsDir
cp -p $projectRootDir/$nativelibsDir/* $stagingNativelibsDir
cp -p -R $projectRootDir/visTool/* $stagingVisToolDir

#
# build stagingDir/configs
#
cp -p $includefile $stagingConfigsDir
cp -p server/deployInfo/* $stagingConfigsDir


function sed_in_place() { if [ "$isMac" = true ]; then sed -i "" "$@"; else sed -i "$@"; fi }

#
# substitute values within vcell.include template from 
# 
stagingVCellInclude=$stagingConfigsDir/vcell.include
sed_in_place "s/GENERATED-SITE/$vcell_site_lower/g"						$stagingVCellInclude
sed_in_place "s/GENERATED-RMIHOST/$vcell_rmihost/g"						$stagingVCellInclude
sed_in_place "s/GENERATED-RMIPORT-LOW/$vcell_rmiport_low/g"				$stagingVCellInclude
sed_in_place "s/GENERATED-RMIPORT-HIGH/$vcell_rmiport_high/g"				$stagingVCellInclude
sed_in_place "s/GENERATED-VCELLSERVICE-JMXPORT/$vcell_vcellservice_jmxport/g"	$stagingVCellInclude
sed_in_place "s/GENERATED-RMISERVICEHIGH-JMXPORT/$vcell_rmiservice_high_jmxport/g"	$stagingVCellInclude
sed_in_place "s/GENERATED-RMISERVICEHTTP-JMXPORT/$vcell_rmiservice_http_jmxport/g"	$stagingVCellInclude
sed_in_place "s/GENERATED-SERVICEHOST/$vcell_servicehost/g"				$stagingVCellInclude
sed_in_place "s/GENERATED-NAGIOSPW/nagcmd/g" 								$stagingVCellInclude
sed_in_place "s/GENERATED-MONITOR-PORT/${vcell_monitor_queryport}/g"		$stagingVCellInclude
sed_in_place "s/GENERATED-JVMDEF/test.monitor.port/g"						$stagingVCellInclude
sed_in_place "s+GENERATED-COMMON-JRE+$vcell_common_jre+g"					$stagingVCellInclude
sed_in_place "s+GENERATED-RMISERVICE-JRE+$vcell_common_jre_rmi+g"			$stagingVCellInclude
sed_in_place "s+GENERATED-NATIVELIBSDIR+$installedNativelibsDir+g"			$stagingVCellInclude
sed_in_place "s+GENERATED-JARSDIR+$installedJarsDir+g"						$stagingVCellInclude
sed_in_place "s+GENERATED-LOGDIR+$installedLogDir+g"						$stagingVCellInclude
sed_in_place "s+GENERATED-CONFIGSDIR+$installedConfigsDir+g"				$stagingVCellInclude
sed_in_place "s+GENERATED-TMPDIR+$installedTmpDir+g"						$stagingVCellInclude
sed_in_place "s+GENERATED-JAVAPREFSDIR+$installedJavaprefsDir+g"			$stagingVCellInclude
sed_in_place "s+GENERATED-JARS+$installedJarsDir/*+g"						$stagingVCellInclude
sed_in_place "s+GENERATED-API-ROOTDIR+$installed_server_sitedir+g"			$stagingVCellInclude
sed_in_place "s+GENERATED-APIKEYSTORE-PATH+$vcell_secrets_tlsKeystore_path+g"	$stagingVCellInclude
sed_in_place "s/GENERATED-APIKEYSTORE-PSWD/$vcell_secrets_tlsKeystore_pswd/g"	$stagingVCellInclude
sed_in_place "s/GENERATED-APIHOST/$vcell_apihost/g"							$stagingVCellInclude
sed_in_place "s/GENERATED-APIPORT/$vcell_apiport/g"							$stagingVCellInclude
sed_in_place "s/GENERATED-VCELLUSER/$vcell_user/g"							$stagingVCellInclude

sed_in_place "s/GENERATED-HTC-USESSH/$vcell_htc_usessh/g"					$stagingVCellInclude
if [ "$vcell_htc_usessh" = true ]; then
	sed_in_place "s/GENERATED-HTC-SSH-HOST/$vcell_htc_sshhost/g"			$stagingVCellInclude
	sed_in_place "s/GENERATED-HTC-SSH-USER/$vcell_htc_sshuser/g"			$stagingVCellInclude
	sed_in_place "s+GENERATED-HTC-SSH-DSAKEYFILE+$vcell_htc_sshDsaKeyFile+g"	$stagingVCellInclude
else
	sed_in_place "s/GENERATED-HTC-SSH-HOST/NOT-DEFINED/g"					$stagingVCellInclude
	sed_in_place "s/GENERATED-HTC-SSH-USER/NOT-DEFINED/g"					$stagingVCellInclude
	sed_in_place "s/GENERATED-HTC-SSH-DSAKEYFILE/NOT-DEFINED/g"			$stagingVCellInclude
fi

if grep -Fq "GENERATED" $stagingVCellInclude
then
    echo "failed to replace all GENERATED tokens in $stagingVCellInclude"
    grep "GENERATED" $stagingVCellInclude
    exit -1
fi

#
# build generated vcell64.properties file - VCell System properties read by PropertyLoader
#
propfile=$stagingConfigsDir/vcell64.properties
if [ -e $propfile ]; then
	rm $propfile
fi
touch $propfile

echo "vcell.server.id = $vcell_site_upper" 									>> $propfile
echo "vcell.softwareVersion = $vcell_softwareVersionString" 				>> $propfile
echo "vcell.installDir = $installed_server_sitedir" 						>> $propfile
echo "vcell.anaconda.installdir = $vcell_anaconda_installdir"				>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#JMS Info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.jms.provider = ActiveMQ"										>> $propfile
echo "vcell.jms.url = $vcell_jms_url" 										>> $propfile
echo "vcell.jms.user = $vcell_jms_user"										>> $propfile
echo "vcell.jms.password = $vcell_secrets_jms_pswd"							>> $propfile
echo "vcell.jms.queue.simReq = simReq$vcell_site_camel"						>> $propfile
echo "vcell.jms.queue.dataReq = simDataReq$vcell_site_camel"				>> $propfile
echo "vcell.jms.queue.dbReq = dbReq$vcell_site_camel"						>> $propfile
echo "vcell.jms.queue.simJob = simJob$vcell_site_camel"						>> $propfile
echo "vcell.jms.queue.workerEvent = workerEvent$vcell_site_camel"			>> $propfile
echo "vcell.jms.topic.serviceControl = serviceControl$vcell_site_camel"		>> $propfile
echo "vcell.jms.topic.daemonControl = daemonControl$vcell_site_camel"		>> $propfile
echo "vcell.jms.topic.clientStatus = clientStatus$vcell_site_camel"			>> $propfile
echo "vcell.jms.blobMessageMinSize = 100000"								>> $propfile
echo "vcell.jms.blobMessageTempDir = $installedJmsBlobFilesDir"				>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Oracle Database Info"												>> $propfile
echo "#"																	>> $propfile
echo "vcell.server.dbConnectURL = $vcell_database_url"						>> $propfile
echo "vcell.server.dbDriverName = $vcell_database_driver"					>> $propfile
echo "vcell.server.dbUserid = $vcell_database_user"							>> $propfile
echo "vcell.server.dbPassword = $vcell_secrets_database_pswd"				>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Amplistor Info"														>> $propfile
echo "#"																	>> $propfile
echo "vcell.amplistor.vcellserviceurl = $vcell_amplistor_url"				>> $propfile
echo "vcell.amplistor.vcellservice.user = $vcell_amplistor_user"			>> $propfile
echo "vcell.amplistor.vcellservice.password = $vcell_secrets_amplistor_pswd"	>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Mongo Info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.mongodb.host = $vcell_mongodb_host"								>> $propfile
echo "vcell.mongodb.port = $vcell_mongodb_port"								>> $propfile
echo "vcell.mongodb.database = $vcell_mongodb_database"						>> $propfile
echo "vcell.mongodb.loggingCollection = $vcell_mongodb_collection"			>> $propfile
echo "vcell.mongodb.threadSleepMS = 10000"									>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Mail Server Info (lost password func)"								>> $propfile
echo "#"																	>> $propfile
echo "vcell.smtp.hostName = $vcell_smtp_host"								>> $propfile
echo "vcell.smtp.port = $vcell_smtp_port"									>> $propfile
echo "vcell.smtp.emailAddress = $vcell_smtp_email"							>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "#Visit Info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.visit.smoldynscript = $installedConfigsDir/convertSmoldyn.py"	>> $propfile
echo "vcell.visit.smoldynvisitexecutable = $installedVisitExe"				>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# java simulation executable"											>> $propfile
echo "#"																	>> $propfile
echo "vcell.javaSimulation.executable = $installedConfigsDir/JavaSimExe64"	>> $propfile
echo "vcell.simulation.preprocessor = $installedConfigsDir/JavaPreprocessor64"	>> $propfile
echo "vcell.simulation.postprocessor = $installedConfigsDir/JavaPostprocessor64"	>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Server configuration"												>> $propfile
echo "#"																	>> $propfile
echo "vcell.primarySimdatadir = $installedPrimarydataDir"					>> $propfile
echo "vcell.secondarySimdatadir = $installedSecondarydataDir"				>> $propfile
echo "vcell.parallelDatadir = $installedParalleldataDir"					>> $propfile
echo "vcell.databaseThreads = 5"											>> $propfile
echo "vcell.exportdataThreads = 3"											>> $propfile
echo "vcell.simdataThreads = 5"												>> $propfile
echo "vcell.htcworkerThreads = 10"											>> $propfile
echo "vcell.export.baseURL = $installedExportUrl"							>> $propfile
echo "vcell.export.baseDir = $installedExportDir/"							>> $propfile
echo "vcell.databaseCacheSize = 50000000"									>> $propfile
echo "vcell.simdataCacheSize = 200000000"									>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Limits"																>> $propfile
echo "#"																	>> $propfile
echo "vcell.limit.jobMemoryMB = 20000"										>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Quota info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.server.maxOdeJobsPerUser = 20"									>> $propfile
echo "vcell.server.maxPdeJobsPerUser = 20"									>> $propfile
echo "vcell.server.maxJobsPerScan = 100"									>> $propfile
echo "vcell.server.maxJobsPerSite = 300"									>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# HTC info"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.htc.logdir = $installedHtclogsDir/"								>> $propfile
echo "vcell.htc.jobMemoryOverheadMB = 70"									>> $propfile
echo "vcell.htc.user = vcell"												>> $propfile
echo "vcell.htc.queue ="													>> $propfile
echo "#vcell.htc.pbs.home ="												>> $propfile
echo "#vcell.htc.sge.home ="												>> $propfile
echo "#vcell.htc.sgeModulePath ="											>> $propfile
echo "#vcell.htc.pbsModulePath ="											>> $propfile
echo "vcell.htc.mpi.home = $vcell_mpich_homedir/"							>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# uncomment to specify which PBS or SGE submission queue to use."		>> $propfile
echo "# when not specified, the default queue is used."						>> $propfile
echo "#vcell.htc.queue = myQueueName"										>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# uncomment to change the Simulation Job Timeout."					>> $propfile
echo "# useful if restart takes a long time, units are in milliseconds"		>> $propfile
echo "#"																	>> $propfile
echo "# here 600000 = 60 * 1000 * 10 = 10 minutes"							>> $propfile
echo "# the default hard-coded in "											>> $propfile
echo "# MessageConstants.INTERVAL_SIMULATIONJOBSTATUS_TIMEOUT_MS"			>> $propfile
echo "#"																	>> $propfile
echo "# vcell.htc.htcSimulationJobStatusTimeout=600000"						>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# Client Timeout in milliseconds"										>> $propfile
echo "#"																	>> $propfile
echo "vcell.client.timeoutMS = 600000"										>> $propfile
echo " "																	>> $propfile
echo "#"																	>> $propfile
echo "# vtk python"															>> $propfile
echo "#"																	>> $propfile
echo "vcell.vtkPythonExecutablePath = $installedPython"						>> $propfile
echo "vcell.visToolPath = $installedVisToolDir"								>> $propfile
echo "##set if needed to pick up python modules"							>> $propfile
echo "#vcell.vtkPythonModulePath ="											>> $propfile

#if [ "$isMac" = true ]; then
#	curl "http://localhost:8080/job/NumericsMulti/platform=macos/lastSuccessfulBuild/artifact/*zip*/archive.zip" -o "$targetRootDir/mac64.zip"
#else
#	curl "http://localhost:8080/job/NumericsMulti/platform=linux64/lastSuccessfulBuild/artifact/*zip*/archive.zip" -o "$targetRootDir/linux64.zip"
#fi

echo "vcell_server_sitedir is $vcell_server_sitedir"
echo "vcell_pathto_sitedir is $vcell_pathto_sitedir"

if [ "$vcell_server_sitedir" == "$vcell_pathto_sitedir" ]
then
	echo "copying to local installation directory"
	mkdir -p $installedSolversDir
	mkdir -p $installedConfigsDir
	mkdir -p $installedVisToolDir
	mkdir -p $installedJarsDir
	mkdir -p $installedNativelibsDir
	mkdir -p $installedInstallersDir
	mkdir -p $installedHtclogsDir
	mkdir -p $installedJmsBlobFilesDir
	mkdir -p $installedLogDir
	mkdir -p $installedTmpDir
	mkdir -p $installedJavaprefsDir
	mkdir -p $installedSystemPrefsDir
	mkdir -p $installedPrimarydataDir
	mkdir -p $installedSecondarydataDir
	mkdir -p $installedParalleldataDir
	mkdir -p $installedExportDir
	mkdir -p $installedDocrootDir
	mkdir -p $installedWebappDir
	cp -p $stagingConfigsDir/*		$installedConfigsDir
	cp -p $stagingJarsDir/*			$installedJarsDir
	cp -p -R $stagingVisToolDir/*	$installedVisToolDir
	cp -p $stagingNativelibsDir/*	$installedNativelibsDir
	cp -p $projectSolversDir/*		$installedSolversDir
	cp -p $stagingInstallersDir/*	$installedInstallersDir
	cp -p -R $apiDocrootDir/*		$installedDocrootDir
	cp -p -R $apiWebappDir/*		$installedWebappDir
	# set execute permissions on scripts
	pushd $installedConfigsDir
	for f in *; do if [ -z "${f//[^.]/}" ]; then chmod +x "$f"; fi done
	popd
	pushd $installedSolversDir
	for f in *; do if [ -z "${f//[^.]/}" ]; then chmod +x "$f"; fi done
	popd
else
	#
	# remote filesystem
	#   don't bother trying to create primary/secondary/parallel data dirs
	#   dont create export directory - probably uses common export directory
	#
	echo "copying to remote installation via shared file system"
	echo "creating dirs"
	mkdir -p $pathto_SolversDir
	mkdir -p $pathto_ConfigsDir
	mkdir -p $pathto_VisToolDir
	mkdir -p $pathto_JarsDir
	mkdir -p $pathto_NativelibsDir
	mkdir -p $pathto_InstallersDir
	mkdir -p $pathto_HtclogsDir
	mkdir -p $pathto_JmsBlobFilesDir
	mkdir -p $pathto_LogDir
	mkdir -p $pathto_TmpDir
	mkdir -p $pathto_JavaprefsDir
	mkdir -p $pathto_SystemPrefsDir
	mkdir -p $pathto_DocrootDir
	mkdir -p $pathto_WebappDir
	#mkdir -p $pathto_PrimarydataDir
	#mkdir -p $pathto_SecondarydataDir
	#mkdir -p $pathto_ParalleldataDir
	#mkdir -p $pathto_ExportDir

	echo "installing scripts to configs (1/8)"
	cp -p $stagingConfigsDir/*		$pathto_ConfigsDir
	echo "installing jar files (2/8)"
	cp -p $stagingJarsDir/*			$pathto_JarsDir
	echo "installing nativelibs (3/8)"
	cp -p $stagingNativelibsDir/*	$pathto_NativelibsDir
	echo "installing visTool python files (4/8)"
	cp -p -R $stagingVisToolDir/*	$pathto_VisToolDir
#	echo "installing server-side solvers (5/8)"
#	cp -p $projectSolversDir/*		$pathto_SolversDir
	echo "installing client installers to server (6/8)"
	cp -p $stagingInstallersDir/*	$pathto_InstallersDir
	echo "installing vcellapi docroot dir to server (7/8)"
	cp -p -R $apiDocrootDir/*		$pathto_DocrootDir
	echo "installing vcellapi webapp dir to server (8/8)"
	cp -p -R $apiWebappDir/*		$pathto_WebappDir
	# set execute permissions on scripts
	pushd $pathto_ConfigsDir
	for f in *; do if [ -z "${f//[^.]/}" ]; then chmod +x "$f"; fi done
	popd
	pushd $pathto_SolversDir
	for f in *; do if [ -z "${f//[^.]/}" ]; then chmod +x "$f"; fi done
	popd
	echo "done with installation"
	
	echo ""
	echo "REMEMBER ... move installers to apache if applicable"
	echo ""
	echo "scp $pathto_InstallersDir/* vcell@apache.cam.uchc.edu:/apache_webroot/htdocs/webstart/$vcell_site_camel"
	echo ""
	echo " then, don't forget to update symbolic links to <latest> installers"
	echo ""
fi
