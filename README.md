# Axonibyte Java Library Suite - Wildcard Library

Copyright (c) 2023 Axonibyte Innovations, LLC. All rights reserved.

This Java library allows for easy pattern matching using `*` and `?` tokens to
denote wildcards. This prevents the need to build out regular expessions. A
finite state machine is used under the hood.

## Documentation

Documentation is in progress, but has not been completed at this time. A wiki
may be provided when documentation has been completed. With this in mind, there
are a few notes regarding tokens to keep in mind:

- the star `*` character refers to one or more of any token
- the question `?` character refers to exactly one of any token
- two or more star characters are interpreted as a single star for processing

## License

This library has been released under the [Apache-2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).
