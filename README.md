# transaction_coordinator </br>

简单的分布式事务协调，跟TCC一样</br>
分为主事务表和事务参与者表</br></br>

1.主事务开始前，先创建一条主事务记录，包含主事务相关的回调和参与者数量</br>
2.开始主事务逻辑</br>
3.调用参与者事务</br>
3.1 参与者先创建参与者记录，和主事务记录关联</br>
3.2 参与者执行逻辑</br>
4.主事务根据远程调用的结果进行本地事务提交或回滚</br>
5.主事务本地事务同步器，根据提交/回滚状态，提交或回滚主事务记录的状态</br>
6.事务协调器，根据主事务的提交或回滚状态，回掉参与者的提交或回滚的回掉方法，方法要幂等</br></br>

异常情况</br>
1.事务参与者创建参与者记录成功，执行自己的逻辑失败，但是返回的时候由于网络原因返回了成功</br>
然后主事务提交了</br>
处理方式：协调器去回调参与者的提交方法时，返回非法请求会返回非法请求，则会认为参与者执行执行失败，</br>
会更新主事务为回滚，并回滚其他参与者的事务
</br>
</br>
</br>
规则：
1.若本地事务已回滚，则回调的回滚方法应该返回成功</br>
2.若本地事务已提交，则回调的提交方法应该返回成功</br>



