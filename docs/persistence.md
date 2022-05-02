# Persistence layer

## Design elements

- Persistence model [object]
  - `Country`, `Airport`, `Airline`, ...
- Common database abstractions [object]
  - `LongBasedTable`, `UuidBaseTable`, `LongTupleBasedTable` ... 
- Aviation database concretions [object]
  - `SlickCountryTable`, `SlickAirportTable`, `TableNames`
- Common Repository abstractions [object]
  - `BaseRepository`
    - `findById`, `delete`, `count`
  - `LongBaseRepository`, `UuidBaseRepository`
- Aviation repository abstractions [package]
  - `CountryRepository`, `AirportRepository`
- Aviation repository concretions [package]
  - `SlickCountryRepository`, `SlickAirportRepository`
- Helpers [object]
  - `implicits`, `adapters`
