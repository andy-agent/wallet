import { Card, Empty } from 'antd';

const Regions = () => {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>区域管理</h2>
      <Card>
        <Empty description="区域管理骨架已就位，待接入真实 Region API" />
      </Card>
    </div>
  );
};

export default Regions;
