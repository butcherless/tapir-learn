package com.cmartin.aviation.port

import com.cmartin.aviation.Commons.ServiceResponse
import com.cmartin.aviation.domain.Model._

trait CountryService {

  /** @param country
    * @return
    */
  def create(country: Country): ServiceResponse[Country]

  /** @param code
    * @return
    */
  def findByCode(code: CountryCode): ServiceResponse[Country]

  /** @param country
    * @return
    */
  def update(country: Country): ServiceResponse[Country]

  /** @param code
    * @return
    */
  def deleteByCode(code: CountryCode): ServiceResponse[Int]
}
