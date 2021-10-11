package com.cmartin.aviation.repository

import com.cmartin.aviation.repository.Model.LongDbo
import zio.Task

trait BaseRepository[E <: LongDbo] {

  def find(id: Long): Task[Option[E]]
  def insert(e: E): Task[Long]
  def insertSeq(seq: Seq[E]): Task[Seq[Long]]
  def update(e: E): Task[Int]
  def count(): Task[Int]

}
