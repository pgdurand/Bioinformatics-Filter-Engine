#Bioinformatics Filtering Engine - Core API

[![Build Status](https://travis-ci.org/pgdurand/Bioinformatics-Filter-Engine.svg?branch=master)](https://travis-ci.org/pgdurand/Bioinformatics-Filter-Engine)
[![License AGPL](https://img.shields.io/badge/license-Affero%20GPL%203.0-blue.svg)](https://www.gnu.org/licenses/agpl-3.0.txt)

##What is a filter?

A filter is made of some rules, each of them being a constraint applied on the data contained in a BLAST result file. This library is capable of reading legacy NCBI XML file (-outfmt 5 argument from BLAST+; -m 7 for legacy BLAST) and creating an object-based data model in memory. This model lets you access the many data fields (Hit ID, alignment length, e-value, scores, etc.) of a BLAST result. So, you use a filter to select relevant hits using your own constraints.

##About this project

This package contains the data filtering library of [BLAST Filter Tool](https://github.com/pgdurand/BLAST-Filter-Tool). It contains the data models to represent [NCBI BLAST results](http://blast.ncbi.nlm.nih.gov/Blast.cgi) and Filters (e.g. *queries*) using hyper-graphs. Indeed, this filtering tool is a concrete implementation of the [Hyper-Graph Explorer](https://github.com/pgdurand/Hyper-Graph-Explorer) applied on BLAST data. 

##Requirements

Use a [Java Virtual Machine](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.7 (or above) from Oracle. 

*Not tested with any other JVM providers but Oracle... so there is no guarantee that the software will work as expected if not using Oracle's JVM.*

##Library uses

See [Wiki](https://github.com/pgdurand/Bioinformatics-Filter-Engine/wiki).

##License and dependencies

"Bioinformatics Filtering Engine - Core API" itself is released under the GNU Affero General Public License, Version 3.0. [AGPL](https://www.gnu.org/licenses/agpl-3.0.txt)

It depends on several thrid-party libraries as stated in the NOTICE.txt file provided with this project.

--
(c) 2006-2016 - Patrick G. Durand
