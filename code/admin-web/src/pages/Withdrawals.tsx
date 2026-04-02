import { Card, Empty } from 'antd';

const Withdrawals = () => {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>提现审核</h2>
      <Card>
        <Empty description="提现审核骨架已就位，待接入真实 Withdrawal API" />
      </Card>
    </div>
  );
};

export default Withdrawals;
