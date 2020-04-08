# jepsen.mysqldemo

## Usage

Simple test for MySQL.

This is adapted from [etcdemo](https://github.com/jepsen-io/jepsen/blob/master/doc/tutorial/index.md).

After building the test database, you can directly run the following command to test.(The sql file is located under the rescue folder)

    lein run test --node databaseIP --test-count 10

or

    lein run test --node dababaseIP --time-limit 60

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
