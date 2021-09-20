package com.cmartin.aviation.repository.zioimpl

import com.cmartin.aviation.repository.Model.LongDbo
import zio.IO

trait BaseRepository[E <: LongDbo] {

  def insert(e: E): IO[Throwable, Long]
  def insert(seq: Seq[E]): IO[Throwable, Seq[Long]]
  def update(e: E): IO[Throwable, Int]
  def count(): IO[Throwable, Int]

}
