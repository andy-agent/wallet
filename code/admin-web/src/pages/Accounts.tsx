import { Card, Empty } from 'antd';

const Accounts = () => {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>用户管理</h2>
      <Card>
        <Empty description="用户列表/详情骨架已就位，待接入真实 Admin Account API" />
      </Card>
    </div>
  );
};

export default Accounts;
