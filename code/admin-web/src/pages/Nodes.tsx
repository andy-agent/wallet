import { Card, Empty } from 'antd';

const Nodes = () => {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>节点管理</h2>
      <Card>
        <Empty description="节点管理骨架已就位，待接入真实 Node API" />
      </Card>
    </div>
  );
};

export default Nodes;
