<?xml version="1.0" encoding="UTF-8"?>
<md:mspec xmlns:md="http://www.eclipse.org/buckminster/MetaData-1.0" 
    installLocation="${install.location}" 
    name="org.eclipse.ecf" 
    materializer="targetPlatform" 
    url="org.eclipse.ecf.sdk.cquery">
    
    <md:mspecNode namePattern="org.eclipse.ecf.*" materializer="workspace"/>
    <md:mspecNode namePattern="ch.ethz.iks.*" materializer="workspace"/>
    <md:mspecNode namePattern="org.jivesoftware.smack" materializer="workspace"/>
</md:mspec>