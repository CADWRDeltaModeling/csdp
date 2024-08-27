---
title: 'Delta Simulation Model 2 Cross-Section Development Program (DSM2 CSDP): A Java application for creating channel geometry for the DSM2 model'
tags:
	- Java
	- California Department of Water Resources
	- CADWR
	- DWR
	- Delta Simulation Model 2
	- DSM2
	- Cross-Section Development Program
	- CSDP
authors:
	- name: Bradley Tom
	orcid: 0009-0009-9034-6577
	equal-contrib: true
	affiliation: 1
	- name: Prabhjot Sandhu
	orcid: 
	equal-contrib: true
	affiliation: 1

# Summary
The Delta Simulation Model 2 (DSM2) Cross-Section Development Program (CSDP)\autoref{fig:csdp_main_window} is a user-friendly Java software tool that is developed to prepare geometric inputs for hydrodynamic models. The CSDP allows the user to use bathymetry data collected in different years by different agencies as a guide to create cross-sections in a river channel manually or automatically.

The process of creating cross-sections with the CSDP involves clicking on a graph of bathymetry data and/or Digital Elevation Model (DEM) data \autoref{fig:csdp_xs_view}. The bathymetry points on the graph can be color-coded to identify attributes such as collection year or data source. The use of a DEM requires more pre-processing of bathymetry data, and can make variations in data quality less obvious to the user. The CSDP also includes a tool to display bathymetry data together with cross-sections in a 3D interactive plot \autoref{fig:csdp_3d_view}, allowing the user to zoom, pan, and rotate the plot to determine qualitatively how the cross-sections compare to the bathymetry data. The 3D plot can also help the user identify features that the user may wish to exclude from model geometry, such as narrow deep holes.

# Statement of need
Hydrodynamic models are widely used in simulating water dynamics in riverine and estuarine systems. A reasonably realistic representation of the geometry (e.g., channel length, junctions, cross sections, etc.) of the study area is imperative for any successful hydrodynamic modeling applications. Typically, hydrodynamic models do not digest these data directly but rely on pre-processing tools to convert the data to a format readable to them.

Cross-sections for 1D models are created exactly or approximately perpendicular to the channel center lines of a river system. The shapes of the cross-sections are derived from bathymetry data, either as individual points, or as a surface created using GIS, such as a DEM. Coordinate systems are channel-based, with cross-section point coordinates measured in the longitudinal and transverse directions. Cross-sections are placed as needed to approximate the variations in the channel bathymetry that are most important for the accuracy of the flow simulation.

Automated approaches to cross-section creation can save time and yield more consistent results. However, they either assume that all data used to create them are of equal quality, or they include a method for making manual adjustments to cross-sections. The CSDP is a standalone application, requiring no other software, such as GIS. It is easy to use, and is designed specifically to create input for DSM2 but also can be applied to other hydrodynamic models. The CSDP differs from tools used for other hydrodynamic models in that the placement of channel centerlines, cross-section lines, and cross-section points can all be done manually. This has the advantage of allowing the user full control over cross-section creation, allowing the user to place more emphasis on bathymetry data considered to be higher quality, and to easily incorporate proposed changes to channels, such as dredging. However, the manual cross-section development process can be time consuming, and results for a given set of input data will be more variable than results from automated tools. The CSDP can also create cross-sections automatically, which can then be edited manually as needed.

affiliations:
  -  name: California Department of Water Resources, USA
   index: 1

date: 23 August 2024

---

# Figures

Figures can be included like this:
![An example graphic user interface window of the CSDP\label{fig:csdp_main_window}](csdpPlanView.png)
![An example cross-section window of the CSDP\label{fig:csdp_xs_view}](csdpXSView.png)
![An example 3D Bathymetry and Cross-Section interactive plot showing river channel bathymetry and user-defined cross-sections. The legend shows the elevation variations of the bathymetry data.\label{fig:csdp_3d_view}](csdp3DView.png)

# Source Code and Packaging
CSDP Source code is available here: https://github.com/CADWRDeltaModeling/csdp
CSDP executable is avialable here: https://data.cnra.ca.gov/dataset/cross-section-development-program-version-3/resource/98471d54-0244-4822-a7cd-2a5cf30636b8

# Acknowledgements

We acknowledge contributions from Ralph Finch during the genesis of this project.

# References

CADWR. Cross-Section Development Program. In Methodology for Flow and Salinity Estimates in the Sacramento-San Joaquin Delta and Suisun Marsh: 19th Annual Progress Report (1998). https://data.cnra.ca.gov/dataset/methodology-for-flow-and-salinity-estimates-in-the-sacramento-san-joaquin-delta-and-suisun-marsh/resource/44093c61-51fe-411f-8425-efc167d1ec6f

CADWR. DSM2 GIS Reference. In Methodology for Flow and Salinity Estimates in the Sacramento-San Joaquin Delta and Suisun Marsh: 41st Annual Progress Report, (2020). https://data.cnra.ca.gov/dataset/methodology-for-flow-and-salinity-estimates-in-the-sacramento-san-joaquin-delta-and-suisun-marsh/resource/a6b3b557-ad17-4490-b9f0-17b88cfb8241
