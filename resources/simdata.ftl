<html>
<head>
<title>Simulation Tasks</title>
</head>
<body>
<#assign simtasklink="/simtask?submitLow=&submitHigh=&startRow=1&maxRows=10&hasData=all&waiting=on&queued=on&dispatched=on&running=on&completed=on&failed=on&stopped=on">
<center><h2><a href="/biomodel">BioModels</a>&nbsp;&nbsp;&nbsp;<a href="${simtasklink}">Simulation Tasks</a>&nbsp;&nbsp;&nbsp;Simulation Data&nbsp;&nbsp;&nbsp;<#if userid?? >(user: ${userid})<#else>(not logged in)</#if></h2></center><br/><center>

</center>
<#assign N=simdata.scanCount>
<h3>Data Values</h3>
<#list 1..N as i>
	<a href='./${simdata.simkey}/jobindex/${i-1}'>jobIndex ${i - 1}</a>&nbsp;
</#list>
<br/>
<br/>
<table border='1'>
<tr>
<th>Variable</th>
</tr>
<#list simdata.variables as variable>
<tr>
<td>${variable.name!""}</td>
</tr>
</#list>
</table>
<br/>
<#if jsonResponse??> JSON response <br/>
${jsonResponse}
<br/>
</#if>

</html>
