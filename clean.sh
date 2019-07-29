#################################################################################
# Copyright (C) 2018-2019  Asurion, LLC
#
# Open Task Scheduler is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Open Task Scheduler is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Open Task Scheduler.  If not, see <https://www.gnu.org/licenses/>.
################################################################################

#!/bin/sh

mvn=$HOME'/tools/apache/apache-maven-3.6.1/bin/mvn'

if [ ! -f "$mvn" ]; then
    echo "$mvn does not exist"
    exit 0
fi

$mvn clean 

install='INSTALL'
rm -rf $install






