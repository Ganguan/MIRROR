# MIRROR
MIRROR is a refactoring recommendation tool, to recommend refactoring by employing a multi-objective optimization across three objectives: (i) improving quality, (ii) removing code smell, and (iii) maximizing the similarity to refactoring history. We envision MIRROR to be used in at least two usage scenarios. First, we claim that MIRROR is especially beneficial in providing recommendations for those attributes with several sub-attributes among which correlations can be quantified. Second, owing to the adoption of numerous types of refactoring, MIRROR is particularly appropriate to recommend various types of refactoring.

# Setup
1 The plug-in is available for the Eclipse Integrated Development Environment (IDE).

2 Installation method: Download the source program from https://github.com/Ganguan/MIRROE, import it to Eclipse, and restart Eclipse.

3 Configuration instructions:(1)need to download the jar package including the asm-5.0.4(https://mvnrepository.com/artifact/org.ow2.asm/asm/5.0.4), jxl-2.6.12(https://mvnrepository.com/artifact/net.sourceforge.jexcelapi/jxl/2.6.12) and the recoder(http://www.java2s.com/Code/Jar/r/Downloadrecorder400jar.htm).(2)use Iplasma(https://github.com/aquaraga/iPlasma) and Refactoring Miner to collect target set.

4 Test program:GanttProject-V1.10.2(https://github.com/bardsoftware/ganttproject),Rhino-V.1.7R1(https://github.com/mozilla/rhino/tree/Rhino1_7R1_BRANCH),JHotDraw-V6.1(https://github.com/wrandelshofer/jhotdraw/tree/jhotdraw60b1-release),ApacheAnt-V1.8.2(https://github.com/apache/ant/tree/rel/1.8.2),Xerces-J-V2.7.0(https://github.com/apache/xerces2-j/tree/Xerces-J_2_7_0) and JFreeChart-V.1.5.2(https://github.com/jfree/jfreechart/tree/v1.5.2).
